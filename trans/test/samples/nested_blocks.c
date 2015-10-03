#include <stdio.h>
#include "wich.h"

void f(Vector * a);

void f(Vector * a)
{
    void *_localptrs[6];

    _localptrs[0] = String_new("cat");
    REF(_localptrs[0]);
    {
        _localptrs[1] = String_new("dog");
        REF(_localptrs[1]);
        {
            _localptrs[2] = String_new("moo");
            REF(_localptrs[2]);
        }
    }
    {
        _localptrs[3] = String_new("boo");
        REF(_localptrs[3]);
        _localptrs[4] = String_new("hoo");
        REF(_localptrs[4]);
    }
    _localptrs[5] = Vector_new((double[]) {7}, 1);
    REF(_localptrs[5]);
}

int main(int argc, char *argv[])
{
    void *_localptrs[0];

    return 0;
}
