#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    VECTOR(x);
    VECTOR(y);
    VECTOR(z);
    VECTOR(q);
    x = Vector_new((double[]) {
                   4, 6, 8}, 3);
    REF(x);
    y = Vector_new((double[]) {
                   2, 3, 4}, 3);
    REF(y);
    z = Vector_mul(x, y);
    REF(z);
    q = Vector_div(z, y);
    REF(q);
    print_vector(q);
    EXIT();
    return 0;
}
