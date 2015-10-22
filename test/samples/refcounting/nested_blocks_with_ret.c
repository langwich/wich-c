#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
int f(PVector_ptr a);

int
f(PVector_ptr a)
{
    ENTER();
    int x;

    STRING(b);
    VECTOR(e);
    REF((void *)a.vector);
    x = 32;
    b = String_new("cat");
    REF((void *)b);
    {
        MARK();
        STRING(c);
        c = String_new("dog");
        REF((void *)c);
        {
            MARK();
            STRING(d);
            d = String_new("moo");
            REF((void *)d);
            {
                EXIT();
                return x;
            }
            RELEASE();
        }
        RELEASE();
    }
    {
        MARK();
        STRING(b);
        b = String_new("boo");
        REF((void *)b);
        RELEASE();
    }
    e = Vector_new((double[]) {7}, 1);
    REF((void *)e.vector);
    EXIT();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    printf("%d\n", f(Vector_new((double[]) {1}, 1)));
    EXIT();
    return 0;
}


