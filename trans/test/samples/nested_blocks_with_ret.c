#include <stdio.h>
#include "wich.h"

int f(Vector * a);

int
f(Vector * a)
{
    int x;
    String *b;
    Vector *e;

    REF(a);

    x = 32;
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
            return x;
        }
    }
    {
        String *b;

        b = String_new("boo");
        REF(b);
    }
    e = Vector_new((double[]) {7}, 1);
    REF(e);
    _deref();
}

int
main(int argc, char *argv[])
{
    printf("%d\n", f(Vector_new((double[]) {1}, 1)));
    return 0;
}
