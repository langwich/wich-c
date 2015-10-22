#include <stdio.h>
#include "wich.h"
PVector_ptr f();

PVector_ptr 
f()
{
    PVector_ptr x;

    x = Vector_new((double[]) {
                   1, 2, 3}, 3);
    return x;
}

int
main(int argc, char *argv[])
{
	setup_error_handlers();
    print_vector(Vector_add(f(), f()));
    return 0;
}
