This is the last of a series of projects I worked on in the Computer Architecture course I took. All four involve the gradual building of a simulation of a hypothetical CPU. This project in particular was about the inclusion of a cache. Another important detail is that this I worked on this, along with the previous three projects, with a partner, where most of the work was done via Zoom, discussing what to do and working on the code in tandem, similar to two people working at the same computer side by side. This is the only project in this portfolio that I did not work on by myself, but despite that, I consider this project (along with the other cooperative Computer Architecture projects) to be the most effort I have put into a programming project during my time at St. Thomas. To read a more detailed description of the simulator, consult the PDF in this directory, as well as the original README.md text below for instructions on running the simulator.

---------------------------------------------------

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
