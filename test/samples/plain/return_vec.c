#include <stdio.h>
#include "wich.h"
PVector_ptr foo();

PVector_ptr 
foo()
{
    return Vector_new((double[]) {1, 2, 3, 4, 5}, 5);
}

int
main(int argc, char *argv[])
{
	setup_error_handlers();
    PVector_ptr x;

    x = foo();
    print_vector(foo());
    return 0;
}
