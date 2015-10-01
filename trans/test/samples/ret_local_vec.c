#include <stdio.h>
#include "wich.h"

Vector *f();

Vector *
f()
{
    Vector *_retv;
    Vector *x;

    x = Vector_new((double[]) {1, 2, 3}, 3);
    REF(x);
    _retv = x;
    REF(_retv);
_cleanup:
    DEREF(_retv);
    DEREF(x);
    return _retv;
}

int
main(int argc, char *argv[])
{
    print_vector(Vector_add(f(), f()));
    return 0;
}
