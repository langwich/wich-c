#include <stdio.h>
#include "wich.h"

f(Vector * a);

f(Vector * a)
{
    String *b;

    Vector *e;

    REF(a);
    b = String_new("cat");
    REF(b);
    {
        String *c;

        c = String_new("dog");
        REF(c);
        {
            String *d;

            d = String_new("moo");
            REF(d);
_cleanup:
            DEREF(d);
        }
_cleanup:
        DEREF(c);
    }
    {
        String *b;

        String *c;

        b = String_new("boo");
        REF(b);
        c = String_new("hoo");
        REF(c);
_cleanup:
        DEREF(b);
        DEREF(c);
    }
    e = Vector_new((double[]) {7}, 1);
    REF(e);
_cleanup:
    DEREF(b);
    DEREF(e);
    DEREF(a);
}

int
main(int argc, char *argv[])
{
    return 0;
}
