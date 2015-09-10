I'm compiling manually at moment:

$ cc -g -I../src gc_retval.c ../src/wich.c

Then testing with valgrind for memory errors

$ valgrind --leak-check=full --show-leak-kinds=all --track-origins=yes ./a.out

valgrind has to be installed on yosemite mac but might be there for earlier OS versions:

$ brew install --HEAD valgrind
