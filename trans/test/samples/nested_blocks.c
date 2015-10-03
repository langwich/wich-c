#include <stdio.h>
#include "wich.h"

void f(Vector * a);

void f(Vector * a)
{
    void *_localptrs[0];

    _localptrs[0] = String_new("cat");
    {
        _localptrs[0] = String_new("dog");
        {
            _localptrs[0] = String_new("moo");
        }
    }
    {
        _localptrs[0] = String_new("boo");
        _localptrs[0] = String_new("hoo");
    }
    _localptrs[0] = Vector_new((double[]) {7}, 1);
}

int main(int argc, char *argv[])
{
    void *_localptrs[0];

    return 0;
}
