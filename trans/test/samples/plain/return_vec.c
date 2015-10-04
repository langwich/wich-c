#include <stdio.h>
#include "wich.h"
Vector *foo();

Vector *
foo()
{
    return Vector_new((double[]) {1, 2, 3, 4, 5}, 5);
}

int
main(int argc, char *argv[])
{
	setup_error_handlers();
    Vector *x;

    x = foo();
    print_vector(foo());
    return 0;
}
