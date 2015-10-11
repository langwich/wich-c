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
    x = Vector_new((double[]) {
                   1, 2, 3}, 3);
    REF(x);
    y = Vector_new((double[]) {
                   4, 5, 6}, 3);
    REF(y);
    print_vector(Vector_add(x, y));
    EXIT();
    return 0;
}