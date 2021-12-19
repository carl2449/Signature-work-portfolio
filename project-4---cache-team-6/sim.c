#include <errno.h>
#include <ctype.h>
#include <math.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#define NUMMEMORY 65536 /* maximum number of data words in memory */
#define NUMREGS 8 /* number of machine registers */

#define ADD 0
#define NAND 1
#define LW 2
#define SW 3
#define BEQ 4
#define JALR 5
#define HALT 6
#define NOOP 7

#define NOOPINSTRUCTION 0x1c00000

typedef struct stateStruct {
	int pc;
	int mem[NUMMEMORY];
	int reg[NUMREGS];
	int numMemory;
	int hits;
	int misses;
} stateType;


typedef struct entryStruct {
	int validBit;
	int dirtyBit;
	int lru;
	int tag;
	int* block;
} entryType;

/*
 * Log the specifics of each cache action.
 *
 * address is the starting word address of the range of data being transferred.
 * size is the size of the range of data being transferred.
 * type specifies the source and destination of the data being transferred.
 *
 * cache_to_processor: reading data from the cache to the processor
 * processor_to_cache: writing data from the processor to the cache
 * memory_to_cache: reading data from the memory to the cache
 * cache_to_memory: evicting cache data by writing it to the memory
 * cache_to_nowhere: evicting cache data by throwing it away
 */
enum action_type {cache_to_processor, processor_to_cache, memory_to_cache, cache_to_memory, cache_to_nowhere};

void print_action(int address, int size, enum action_type type)
{
	printf("transferring word [%i-%i] ", address, address + size - 1);
	if (type == cache_to_processor) {
		printf("from the cache to the processor\n");
	} else if (type == processor_to_cache) {
		printf("from the processor to the cache\n");
	} else if (type == memory_to_cache) {
		printf("from the memory to the cache\n");
	} else if (type == cache_to_memory) {
		printf("from the cache to the memory\n");
	} else if (type == cache_to_nowhere) {
		printf("from the cache to nowhere\n");
	}
}

int field0(int instruction){
	return( (instruction>>19) & 0x7);
}

int field1(int instruction){
	return( (instruction>>16) & 0x7);
}

int field2(int instruction){
	return(instruction & 0xFFFF);
}

int opcode(int instruction){
	return(instruction>>22);
}

void printInstruction(int instr){
	char opcodeString[10];
	if (opcode(instr) == ADD) {
		strcpy(opcodeString, "add");
	} else if (opcode(instr) == NAND) {
		strcpy(opcodeString, "nand");
	} else if (opcode(instr) == LW) {
		strcpy(opcodeString, "lw");
	} else if (opcode(instr) == SW) {
		strcpy(opcodeString, "sw");
	} else if (opcode(instr) == BEQ) {
		strcpy(opcodeString, "beq");
	} else if (opcode(instr) == JALR) {
		strcpy(opcodeString, "jalr");
	} else if (opcode(instr) == HALT) {
		strcpy(opcodeString, "halt");
	} else if (opcode(instr) == NOOP) {
		strcpy(opcodeString, "noop");
	} else {
		strcpy(opcodeString, "data");
	}

	printf("%s %d %d %d\n", opcodeString, field0(instr), field1(instr),
			field2(instr));
}

void printState(stateType *statePtr){
	int i;
	printf("\n@@@\nstate:\n");
	printf("\tpc %d\n", statePtr->pc);
	printf("\tmemory:\n");
	for(i = 0; i < statePtr->numMemory; i++){
		printf("\t\tmem[%d]=%d\n", i, statePtr->mem[i]);
	}	
	printf("\tregisters:\n");
	for(i = 0; i < NUMREGS; i++){
		printf("\t\treg[%d]=%d\n", i, statePtr->reg[i]);
	}
	printf("end state\n");
}

void printCache(entryType** cache, int rows, int columns, int blockOffsetBits) {
	int setIndexBits = log(rows) / log(2);
	int startAddress;
	for (int i = 0; i < rows; i++) {
		printf("| ");
		for (int j = 0; j < columns; j++) {
			if (cache[i][j].validBit == 0) {
				printf("- | ");
			} else {
				startAddress = ((cache[i][j].tag << setIndexBits) + i) << blockOffsetBits; 
				printf("%d | ", startAddress);
			}
		}
		printf("\n");
	}
}

int signExtend(int num){
	// convert a 16-bit number into a 32-bit integer
	if (num & (1<<15) ) {
		num -= (1<<16);
	}
	return num;
}

void print_stats(stateType* state){
	printf("Hits: %d\n", state->hits);
	printf("Misses: %d\n", state->misses);
}

int selectBlockOffset(int blockOffsetBits, int address) {
	return address & ((1 << blockOffsetBits) - 1);
}

int selectSetIndex(int setIndexBits, int blockOffsetBits, int address) {
	return ((address >> blockOffsetBits) & ((1 << setIndexBits) - 1));
}

int selectTag(int setIndexBits, int blockOffsetBits, int address) {
	return address >> (setIndexBits + blockOffsetBits);
}

void run(stateType* state, entryType** cache, int sets, int ways, int blkSize){

	// Reused variables;
	int instr = 0;
	int regA = 0;
	int regB = 0;
	int offset = 0;
	int branchTarget = 0;
	int aluResult = 0;

	int blockSize = blkSize;
	int blockOffsetBits = log(blockSize) / log(2);// int > block off
	int numberOfSets = sets;
	int setIndexBits = log(numberOfSets) / log(2); // int > setindex
	int setIndex;
	int blockOffset;
	int associativity = ways;	
	int isFound = 0;
	int allValid = 0;
	int wayIndex = 0;

	int total_instrs = 0;

	// Primary loop
	while(1){
		total_instrs++;

		//printState(state);

		// Instruction Fetch
		instr = state->mem[state->pc];
		//Cache update here
		setIndex = selectSetIndex(setIndexBits, blockOffsetBits, state->pc);
		blockOffset = selectBlockOffset(blockOffsetBits, state->pc);
		
		isFound = 0;
		for(int i = 0; i < associativity; i++) {
			//currentEntry = cache[setIndex][i];
			if (cache[setIndex][i].validBit == 1 && cache[setIndex][i].tag == selectTag(setIndexBits, blockOffsetBits, state->pc)) {
				isFound = 1;
				state->hits++;
				for (int j = 0; j < associativity; j++) {
					if(cache[setIndex][j].validBit == 1 && cache[setIndex][j].tag != cache[setIndex][i].tag && cache[setIndex][j].lru <= cache[setIndex][i].lru) {
						cache[setIndex][j].lru++;
					}
				}	
				wayIndex = i;
				cache[setIndex][wayIndex].lru = 0;
				break;
			}			

		}

		if (isFound == 0) {
			//insertion
			state->misses++;
			for(int i = 0; i < associativity; i++) {
				if (cache[setIndex][i].validBit == 0) {
					cache[setIndex][i].tag = selectTag(setIndexBits, blockOffsetBits, state->pc);
					cache[setIndex][i].validBit = 1;
					cache[setIndex][i].lru = 0;
					for (int j = 0; j < blockSize; j++) {
						cache[setIndex][i].block[j] = state->mem[(state->pc-blockOffset) + j];
					}
					//updates LRU values for everything except what was just inserted
					for (int j = 0; j < associativity; j++) {
						if(cache[setIndex][j].validBit == 1 && cache[setIndex][j].tag != cache[setIndex][i].tag) {
							cache[setIndex][j].lru++;
						}
					}
					break;
				}

				if (cache[setIndex][i].lru == associativity - 1) {
					//insertEntry = cache[setIndex][i];
					int startAddress = ((cache[setIndex][i].tag << setIndexBits) + setIndex) << blockOffsetBits;
					if (cache[setIndex][i].dirtyBit == 1) {
						for (int j = 0; j < blockSize; j++) {
							state->mem[startAddress + j] = cache[setIndex][i].block[j];
						}
						print_action(startAddress, blockSize, cache_to_memory);
					} else {
						print_action(startAddress, blockSize, cache_to_nowhere);
					}

					cache[setIndex][i].tag = selectTag(setIndexBits, blockOffsetBits, state->pc);
					cache[setIndex][i].lru = 0;
					cache[setIndex][i].dirtyBit = 0;
					for (int j = 0; j < blockSize; j++) {
						cache[setIndex][i].block[j] = state->mem[(state->pc-blockOffset) + j];
					}
					//updates LRU values for everything except what was just inserted
					for (int j = 0; j < associativity; j++) {
						if(cache[setIndex][j].validBit == 1 && cache[setIndex][j].tag != cache[setIndex][i].tag && cache[setIndex][j].lru <= cache[setIndex][i].lru) {
							cache[setIndex][j].lru++;
						}
					}
					break;
				}
				
			}
			print_action(state->pc - blockOffset, blockSize, memory_to_cache);
			//printCache(cache, numberOfSets, associativity, blockOffsetBits);			

		}
		print_action(state->pc, 1, cache_to_processor);

		/* check for halt */
		if (opcode(instr) == HALT) {
			for (int i = 0; i < numberOfSets; i++) {
				for (int j = 0; j < associativity; j++) {
					if (cache[i][j].dirtyBit == 1 && cache[i][j].validBit == 1) {
						int startAddress = ((cache[i][j].tag << setIndexBits) + i) << blockOffsetBits;
						for (int k = 0; k < blockSize; k++) {
							state->mem[startAddress + j] = cache[i][j].block[k];
						}
						print_action(startAddress, blockSize, cache_to_memory);
					}

					cache[i][j].validBit = 0;
				}
			}
			//printf("machine halted\n");
			break;
		}

		// Increment the PC
		state->pc = state->pc+1;

		// Set reg A and B
		regA = state->reg[field0(instr)];
		regB = state->reg[field1(instr)];

		// Set sign extended offset
		offset = signExtend(field2(instr));

		// Branch target gets set regardless of instruction
		branchTarget = state->pc + offset;

		/**
		 *
		 * Action depends on instruction
		 *
		 **/
		// ADD
		if(opcode(instr) == ADD){
			// Add
			aluResult = regA + regB;
			// Save result
			state->reg[field2(instr)] = aluResult;
		}
		// NAND
		else if(opcode(instr) == NAND){
			// NAND
			aluResult = ~(regA & regB);
			// Save result
			state->reg[field2(instr)] = aluResult;
		}
		// LW or SW
		else if(opcode(instr) == LW || opcode(instr) == SW){
			// Calculate memory address
			aluResult = regB + offset;
			if(opcode(instr) == LW){
				setIndex = selectSetIndex(setIndexBits, blockOffsetBits, aluResult);
				blockOffset = selectBlockOffset(blockOffsetBits, aluResult);
				isFound = 0;
				for (int i = 0; i < associativity; i++) {
					if (cache[setIndex][i].validBit == 1 && cache[setIndex][i].tag == selectTag(setIndexBits, blockOffsetBits, aluResult)) {
						isFound = 1;
						state->hits++;
						for (int j = 0; j < associativity; j++) {
							if(cache[setIndex][j].validBit == 1 && cache[setIndex][j].tag != cache[setIndex][i].tag && cache[setIndex][j].lru <= cache[setIndex][i].lru) {
								cache[setIndex][j].lru++;
							}
						}
						wayIndex = i;
						cache[setIndex][wayIndex].lru = 0;
						break;
					}
				}	
				
				if (isFound == 0) {
					state->misses++;
					for(int i = 0; i < associativity; i++) {
						if (cache[setIndex][i].validBit == 0) {
							cache[setIndex][i].tag = selectTag(setIndexBits, blockOffsetBits, aluResult);
							cache[setIndex][i].validBit = 1;
							cache[setIndex][i].lru = 0;
							for (int j = 0; j < blockSize; j++) {
								cache[setIndex][i].block[j] = state->mem[(aluResult-blockOffset) + j];
							}
							print_action(aluResult-blockOffset, blockSize, memory_to_cache);
							//printCache(cache, numberOfSets, associativity, blockOffsetBits);
							wayIndex = i;
							//updates LRU values for everything except what was just inserted
							for (int j = 0; j < associativity; j++) {
								if(cache[setIndex][j].validBit == 1 && cache[setIndex][j].tag != cache[setIndex][i].tag) {
									cache[setIndex][j].lru++;
								}
							}
							break;
						}


						if (cache[setIndex][i].lru == associativity - 1) {
							int startAddress = ((cache[setIndex][i].tag << setIndexBits) + setIndex) << blockOffsetBits;
							if (cache[setIndex][i].dirtyBit == 1) {
								for (int j = 0; j < blockSize; j++) {
									state->mem[startAddress + j] = cache[setIndex][i].block[j];
								}
								print_action(startAddress, blockSize, cache_to_memory);
							} else {
								print_action(startAddress, blockSize, cache_to_nowhere);
							}

							cache[setIndex][i].tag = selectTag(setIndexBits, blockOffsetBits, aluResult);
							cache[setIndex][i].lru = 0;
							cache[setIndex][i].dirtyBit = 0;
							for (int j = 0; j < blockSize; j++) {
								cache[setIndex][i].block[j] = state->mem[(aluResult-blockOffset) + j];
							}
							//updates LRU values for everything except what was just inserted
							for (int j = 0; j < associativity; j++) {
								if(cache[setIndex][j].validBit == 1 && cache[setIndex][j].tag != cache[setIndex][i].tag && cache[setIndex][j].lru <= cache[setIndex][i].lru) {
									cache[setIndex][j].lru++;
								}	
							}
							print_action(aluResult-blockOffset, blockSize, memory_to_cache);
							//printCache(cache, numberOfSets, associativity, blockOffsetBits);
							wayIndex = i;
							break;
						} 
						
					}


				}

				state->reg[field0(instr)] = cache[setIndex][wayIndex].block[blockOffset];
				print_action(aluResult, 1, cache_to_processor);
			}else if(opcode(instr) == SW){
				// Store
				setIndex = selectSetIndex(setIndexBits, blockOffsetBits, aluResult);
				blockOffset = selectBlockOffset(blockOffsetBits, aluResult);
				isFound = 0;
				for (int i = 0; i < associativity; i++) {
					if (cache[setIndex][i].validBit == 1 && cache[setIndex][i].tag == selectTag(setIndexBits, blockOffsetBits, aluResult)) {
						isFound = 1;
						state->hits++;
						for (int j = 0; j < associativity; j++) {
							if(cache[setIndex][j].validBit == 1 && cache[setIndex][j].tag != cache[setIndex][i].tag && cache[setIndex][j].lru <= cache[setIndex][i].lru) {
								cache[setIndex][j].lru++;
							}
						}
						wayIndex = i;
						cache[setIndex][wayIndex].lru = 0;
						break;
					}
				}
				
				if (isFound == 0) {
					state->misses++;
					for(int i = 0; i < associativity; i++) {
						if (cache[setIndex][i].validBit == 0) {
							cache[setIndex][i].tag = selectTag(setIndexBits, blockOffsetBits, aluResult);
							cache[setIndex][i].validBit = 1;
							cache[setIndex][i].dirtyBit = 1;
							cache[setIndex][i].lru = 0;
							for (int j = 0; j < blockSize; j++) {
								cache[setIndex][i].block[j] = state->mem[(aluResult-blockOffset) + j];
							}
							print_action(aluResult-blockOffset, blockSize, memory_to_cache);
							//printCache(cache, numberOfSets, associativity, blockOffsetBits);
							wayIndex = i;
							//updates LRU values for everything except what was just inserted
							for (int j = 0; j < associativity; j++) {
								if(cache[setIndex][j].validBit == 1 && cache[setIndex][j].tag != cache[setIndex][i].tag) {
									cache[setIndex][j].lru++;
								}
							}
							break;
						}

						if (cache[setIndex][i].lru == associativity - 1) {
							//insertEntry = cache[setIndex][i];
							int startAddress = ((cache[setIndex][i].tag << setIndexBits) + setIndex) << blockOffsetBits;

							if (cache[setIndex][i].dirtyBit == 1) {
								for (int j = 0; j < blockSize; j++) {
									state->mem[startAddress + j] = cache[setIndex][i].block[j];
								}
								print_action(startAddress, blockSize, cache_to_memory);
							} else {
								print_action(startAddress, blockSize, cache_to_nowhere);
							}

							cache[setIndex][i].tag = selectTag(setIndexBits, blockOffsetBits, aluResult);
							cache[setIndex][i].lru = 0;
							cache[setIndex][i].dirtyBit = 1;
							for (int j = 0; j < blockSize; j++) {
								cache[setIndex][i].block[j] = state->mem[(aluResult-blockOffset) + j];
							}
							//updates LRU values for everything except what was just inserted
							for(int j = 0; j < associativity; j++){
								if(cache[setIndex][j].validBit == 1 && cache[setIndex][j].tag != cache[setIndex][i].tag && cache[setIndex][j].lru <= cache[setIndex][i].lru) {
									cache[setIndex][j].lru++;
								}		
							}
							print_action(aluResult-blockOffset, blockSize, memory_to_cache);
							//printCache(cache, numberOfSets, associativity, blockOffsetBits);
							wayIndex = i;
							break;
						}
						
					}
					
				}
				cache[setIndex][wayIndex].block[blockOffset] = regA;
				cache[setIndex][wayIndex].dirtyBit = 1;
				print_action(aluResult, 1, processor_to_cache);				

			}
		}
		// JALR
		else if(opcode(instr) == JALR){
			// rA != rB for JALR to work
			// Save pc+1 in regA
			state->reg[field0(instr)] = state->pc;
			//Jump to the address in regB;
			state->pc = state->reg[field1(instr)];
		}
		// BEQ
		else if(opcode(instr) == BEQ){
			// Calculate condition
			aluResult = (regA - regB);

			// ZD
			if(aluResult==0){
				// branch
				state->pc = branchTarget;
			}
		}	
	} // While
	print_stats(state);
}

int main(int argc, char** argv){

	/** Get command line arguments **/
	char* fname;

	opterr = 0;

	int cin = 0;
	int block_size_in_words;
	int number_of_sets;
	int associativity;


	while((cin = getopt(argc, argv, "f:b:s:a:")) != -1){
		switch(cin)
		{
			case 'f':
				fname=(char*)malloc(strlen(optarg));
				fname[0] = '\0';

				strncpy(fname, optarg, strlen(optarg)+1);
				//printf("FILE: %s\n", fname);
				break;
			case 'b':
				block_size_in_words = atoi(optarg);	
				break;
			case 's':
				number_of_sets = atoi(optarg);
				break;
			case 'a':
				associativity = atoi(optarg);
				break;
			case '?':
				if(optopt == 'f' || optopt == 'b' || optopt == 's' || optopt == 'a'){
					printf("Option -%c requires an argument.\n", optopt);
				}
				else if(isprint(optopt)){
					printf("Unknown option `-%c'.\n", optopt);
				}
				else{
					printf("Unknown option character `\\x%x'.\n", optopt);
					return 1;
				}
				break;
			default:
				abort();
		}
	}

	/*
	   if ((log(block_size_in_words) / log(2)) % 1 != 0 || (log(number_of_sets) / log(2)) % 1 != 0 || (log(associativity) / log(2)) % 1 != 0) {
	   printf("ERROR: Block size, sets, and associativity must all be powers of 2\n");
	   return -1;
	   }
	 */

	FILE *fp = fopen(fname, "r");
	if (fp == NULL) {
		printf("Cannot open file '%s' : %s\n", fname, strerror(errno));
		return -1;
	}

	/* count the number of lines by counting newline characters */
	int line_count = 0;
	int c;
	while (EOF != (c=getc(fp))) {
		if ( c == '\n' ){
			line_count++;
		}
	}
	// reset fp to the beginning of the file
	rewind(fp);
	//Creates dynamically sized 2-D Array
	entryType** cache = (entryType**)malloc(number_of_sets* sizeof(entryType*));
	for (int i = 0; i < number_of_sets; i++) {
		cache[i] = (entryType*)malloc(associativity* sizeof(entryType));
	}
	for (int i = 0; i < number_of_sets; i++) {
		for (int j = 0; j < associativity; j++) {
			cache[i][j].validBit = 0;
			cache[i][j].dirtyBit = 0;
			cache[i][j].lru = 0;
			cache[i][j].tag = 0;
			cache[i][j].block = (int*)malloc(block_size_in_words* sizeof(int));
		}
	} 	

	stateType* state = (stateType*)malloc(sizeof(stateType));

	state->pc = 0;
	memset(state->mem, 0, NUMMEMORY*sizeof(int));
	memset(state->reg, 0, NUMREGS*sizeof(int));

	state->numMemory = line_count;

	char line[256];

	int i = 0;
	while (fgets(line, sizeof(line), fp)) {
		/* note that fgets doesn't strip the terminating \n, checking its
		   presence would allow to handle lines longer that sizeof(line) */
		state->mem[i] = atoi(line);
		i++;
	}
	fclose(fp);

	/** Run the simulation **/
	run(state, cache, number_of_sets, associativity, block_size_in_words);

	free(state);
	free(fname);
	free(cache);

}

