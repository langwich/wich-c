#include <stdio.h>
#include "wich.h"

void f(Vector * a);

void f(Vector * a)
{
    String *b;
    _heapvar((heap_object **) & b);

    Vector *e;
    _heapvar((heap_object **) & e);

    REF(a);

    b = String_new("cat");
    REF(b);
    {
        String *c;
        _heapvar((heap_object **) & c);

        c = String_new("dog");
        REF(c);
        {
            String *d;
            _heapvar((heap_object **) & d);

            d = String_new("moo");
            REF(d);
        }
    }
    {
        String *b;
        _heapvar((heap_object **) & b);

        String *c;
        _heapvar((heap_object **) & c);

        b = String_new("boo");
        REF(b);
        c = String_new("hoo");
        REF(c);
    }
    e = Vector_new((double[]) {7}, 1);
    REF(e);
    _deref();
}

int main(int argc, char *argv[])
{
    return 0;
}
