#include <stdio.h>
#include "wich.h"
int
main(int ____c, char *____v[])
{
    setup_error_handlers();
    PVector_ptr x;

    PVector_ptr y;

    x = Vector_new((double[]) {
                   1, 2, 3}, 3);
    y = Vector_new((double[]) {
                   1, 2, 3, 4}, 4);
    print_vector(Vector_mul(x, y));
    return 0;
}