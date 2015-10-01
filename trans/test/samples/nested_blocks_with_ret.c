#include <stdio.h>
#include <stdbool.h>
#include "wich.h"

int f(Vector *a);

int f(Vector *_a)
{
#define a  0
#define b  1
#define c  2
#define d  3
#define b1 4
#define e  5
    heap_object _localptrs[6]; // room for all ptrs simultaneously including args

    _localptrs[a] = _a;        // copy arg into locals array
    REF(_localptrs[a]);

    int x;                     // primitives can stay as-is
    x = 32;

    _localptrs[b] = String_new("cat");
    REF(_localptrs[b]);
    {
        _localptrs[_c] = String_new("dog");
        REF(_localptrs[_c]);
        {
            _localptrs[_d] = String_new("moo");
            REF(_localptrs[_d]);
            deref(_localptrs);
            return x;
        }
    }
    {
        _localptrs[_b1] = String_new("boo");
        REF(_localptrs[_b1]);
    }
    _localptrs[_e] = Vector_new((double[]) {7}, 1);
    REF(_localptrs[_e]);
    deref(_localptrs);
#undef a
#undef b
#undef c
#undef d
#undef b1
#undef e
}

int
main(int argc, char *argv[])
{
    printf("%d\n", f(Vector_new((double[]) {1}, 1)));
    return 0;
}
