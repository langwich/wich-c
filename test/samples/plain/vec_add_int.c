#include <stdio.h>
#include "wich.h"
Vector *f(int x);

Vector *
f(int x)
{
    Vector *y;

    Vector *z;

    y = Vector_new((double[]) {
                   1, 2, 3}, 3);
    z = Vector_add(y, Vector_from_int(x, y));
    return z;
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    print_vector(f(4));
    return 0;
}
