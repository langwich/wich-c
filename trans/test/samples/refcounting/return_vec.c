#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
Vector *foo();

Vector *
foo()
{
    ENTER();
    {
        EXIT();
        return Vector_new((double[]) {1, 2, 3, 4, 5}, 5);
    }
    EXIT();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    VECTOR(x);
    x = foo();
    REF(x);
    print_vector(foo());
    EXIT();
    return 0;
}
