#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

void f(int x, PVector_ptr  v);

void
f(int x, PVector_ptr  v)
{
    ENTER();
    REF(v);
    EXIT();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    EXIT();
    return 0;
}
