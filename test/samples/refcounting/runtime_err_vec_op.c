#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
int
main(int ____c, char *____v[])
{
    setup_error_handlers();
    ENTER();
    VECTOR(x);
    VECTOR(y);
    x = Vector_new((double[]) {
                   1, 2, 3}, 3);
    REF((void *)x.vector);
    y = Vector_new((double[]) {
                   1, 2, 3, 4}, 4);
    REF((void *)y.vector);
    print_vector(Vector_mul(x, y));
    EXIT();
    return 0;
}