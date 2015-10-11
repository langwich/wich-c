#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
Vector *f(int x);

Vector *
f(int x)
{
    ENTER();
    VECTOR(y);
    VECTOR(z);
    y = Vector_new((double[]) {
                   1, 2, 3}, 3);
    REF(y);
    z = Vector_add(y, x);
    REF(z);
    {
        REF(z);
        EXIT();
        DEC(z);
        return z;
    }
    EXIT();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    print_vector(f(4));
    EXIT();
    return 0;
}