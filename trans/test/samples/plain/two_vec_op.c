#include <stdio.h>
#include "wich.h"
int
main(int argc, char *argv[])
{
    setup_error_handlers();
    Vector *x;

    Vector *y;

    Vector *z;

    Vector *q;

    x = Vector_new((double[]) {
                   4, 6, 8}, 3);
    y = Vector_new((double[]) {
                   2, 3, 4}, 3);
    z = Vector_mul(x, y);
    q = Vector_div(z, y);
    print_vector(q);
    return 0;
}