#include <stdio.h>
#include "wich.h"

void f();

void f()
{
    void *_localptrs[3];

    _localptrs[0] = String_new("cat");
    REF(_localptrs[0]);
    {
        _localptrs[1] = String_new("dog");
        REF(_localptrs[1]);
        _localptrs[2] = _localptrs[0];
        REF(_localptrs[2]);
    }

}

int main(int argc, char *argv[])
{
    void *_localptrs[0];

    f();
    return 0;
}
