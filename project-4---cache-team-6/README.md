Run Instructions:
./sim -f [input machine code file] -b [block size in words] -s [number of sets] -a [number of ways per set]

Files:

sim.c: takes machine code as input and outputs formatted cache actions
Makefile: for compiling sim.c

/tests:  .mc files for testing the simulator (plus the .asm files they are derived from)

class.mc: reference code provided, compiled to machine code
directmapped.4.16.1.mc: tests the functionality of a directly mapped cache
fullassociative.4.1.16.mc: tests the functionality of a fully-associative cache
lru.4.4.4.mc: tests the functionality of the LRU and eviction events
maxsize.1.16.16.mc: tests the functionality of a cache at max size (256 blocks)
writeback.2.8.2.mc: tests the functionality of writing back to memory in the eviction of a block modified by a SW instruction, both prior to another block replacing it and upon reaching a halt
