#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

void f();

void
f()
{
    ENTER();
    STRING(x);
    x = String_new("cat");
    REF(x);
    {
        MARK();
        STRING(y);
        STRING(z);
        y = String_new("dog");
        REF(y);
        z = x;
        REF(z);
        RELEASE();
    }
    EXIT();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    f();
    EXIT();
    return 0;
}
