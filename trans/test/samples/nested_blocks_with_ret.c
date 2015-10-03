#include <stdio.h>
#include "wich.h"

int f(Vector * a);

int
f(Vector * a)
{
    void *_localptrs[5];

    int x;

    x = 32;
    _localptrs[1] = String_new("cat");
    REF(_localptrs[1]);
    {
        _localptrs[2] = String_new("dog");
        REF(_localptrs[2]);
        {
            _localptrs[3] = String_new("moo");
            REF(_localptrs[3]);
            return x;
        }
    }
    {
        _localptrs[4] = String_new("boo");
        REF(_localptrs[4]);
    }
    _localptrs[5] = Vector_new((double[]) {7}, 1);
    REF(_localptrs[5]);
    deref(_localptrs);
}

int
main(int argc, char *argv[])
{
    void *_localptrs[0];

    printf("%d\n", f(Vector_new((double[]) {1}, 1)));
    deref(_localptrs);
    return 0;
}
