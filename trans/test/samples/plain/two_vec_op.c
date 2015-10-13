#include <stdio.h>
#include "wich.h"
int
main(int argc, char *argv[])
{
    setup_error_handlers();
    Vector *x;

    Vector *y;

    Vector *z;

    x = Vector_new((double[]) {
                   4, 6, 8}, 3);
    y = Vector_new((double[]) {
                   2, 3, 4}, 3);
    z = Vector_div(Vector_mul(Vector_add(x, y), x), y);
    print_vector(z);
    return 0;
}

