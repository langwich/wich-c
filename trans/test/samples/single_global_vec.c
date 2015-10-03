#include <stdio.h>
#include "wich.h"

int
main(int argc, char *argv[])
{
    void *_localptrs[1];

    _localptrs[0] = Vector_new((double[]) {1, 2, 3, 4, 5}, 5);
    REF(_localptrs[0]);
    deref(_localptrs);
    return 0;
}
