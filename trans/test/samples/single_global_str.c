#include <stdio.h>
#include "wich.h"

int
main(int argc, char *argv[])
{
    void *_localptrs[1];

    _localptrs[0] = String_new("Hello World!");
    REF(_localptrs[0]);
    deref(_localptrs);
    return 0;
}
