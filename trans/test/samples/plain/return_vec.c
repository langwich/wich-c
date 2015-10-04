#include <stdio.h>
#include "wich.h"
Vector *foo();

Vector *
foo()
{
    Vector *y;

    y = Vector_new((double[]) {
                   1, 2, 3, 4, 5}, 5);
    return y;
}

int
main(int argc, char *argv[])
{
    Vector *x;

    x = foo();
    print_vector(foo());
    return 0;
}
