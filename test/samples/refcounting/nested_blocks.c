#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
void f(PVector_ptr  a);

void
f(PVector_ptr  a)
{
    ENTER();
    STRING(b);
    VECTOR(e);
    REF(a);
    b = String_new("cat");
    REF(b);
    {
        MARK();
        STRING(c);
        c = String_new("dog");
        REF(c);
        {
            MARK();
            STRING(d);
            d = String_new("moo");
            REF(d);
            RELEASE();
        }
        RELEASE();
    }
    {
        MARK();
        STRING(b);
        STRING(c);
        b = String_new("boo");
        REF(b);
        c = String_new("hoo");
        REF(c);
        RELEASE();
    }
    e = Vector_new((double[]) {7}, 1);
    REF(e);
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
