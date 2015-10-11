#include <stdio.h>
#include "wich.h"
int
main(int argc, char *argv[])
{
    setup_error_handlers();
    Vector *x;

    Vector *y;

    x = Vector_new((double[]) {
                   1, 2, 3}, 3);
    y = Vector_new((double[]) {
                   4, 5, 6}, 3);
    print_vector(Vector_add(x, y));
    return 0;
}