#include <stdio.h>
#include "wich.h"
Vector *f();

Vector *
f()
{
    Vector *x;

    x = Vector_new((double[]) {
                   1, 2, 3}, 3);
    return x;
}

int
main(int argc, char *argv[])
{
    print_vector(Vector_add(f(), f()));
    return 0;
}
