CC=gcc

all: sim.o
	$(CC) sim.o -o sim -lm

sim.o: sim.c
	$(CC) -c sim.c -o sim.o -lm

clean:
	rm -Rf sim.o sim
