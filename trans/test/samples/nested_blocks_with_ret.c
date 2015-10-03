#include <stdio.h>
#include "wich.h"

int f(Vector * a);

int f(Vector * a)
{
    void *_localptrs[0];
    int x;

    x = 32;
    _localptrs[0] = String_new("cat");
    {
        _localptrs[0] = String_new("dog");
        {
            _localptrs[0] = String_new("moo");
            return x;
        }
    }
    {
        _localptrs[0] = String_new("boo");
    }
    _localptrs[0] = Vector_new((double[]) {7}, 1);
}

int main(int argc, char *argv[])
{
    void *_localptrs[0];

    printf("%d\n", f(Vector_new((double[]) {1}, 1)));
    return 0;
}
