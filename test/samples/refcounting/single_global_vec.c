#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    VECTOR(x);
    x = Vector_new((double[]) {
                   1, 2, 3, 4, 5}, 5);
    REF(x);
    EXIT();
    return 0;
}
