#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
Vector *f();

Vector *
f()
{
    ENTER();
    VECTOR(x);
    x = Vector_new((double[]) {
                   1, 2, 3}, 3);
    REF(x);
    {
        REF(x);
        EXIT();
        DEC(x);
        return x;
    }
    EXIT();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    print_vector(Vector_add(f(), f()));
    EXIT();
    return 0;
}
