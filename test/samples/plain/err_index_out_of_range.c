#include <stdio.h>
#include "wich.h"
int
main(int ____c, char *____v[])
{
    setup_error_handlers();
    PVector_ptr x;

    x = Vector_new((double[]) {
                   1, 2, 3, 4}, 4);
    set_ith(x, 6 - 1, 5);
    print_vector(x);
    return 0;
}