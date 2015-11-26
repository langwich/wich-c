#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
int
main(int ____c, char *____v[])
{
    setup_error_handlers();
    ENTER();
    VECTOR(x);
    x = Vector_new((double[]) {
                   1, 2, 3, 4}, 4);
    REF((void *)x.vector);
    set_ith(x, 6 - 1, 5);
    print_vector(x);
    EXIT();
    return 0;
}