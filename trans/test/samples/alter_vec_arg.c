#include <stdio.h>
#include "wich.h"

void bar(Vector * x);

void bar(Vector * x)
{
    void *_localptrs[0];

    COPY_ON_WRITE(_localptrs[0]);
    ((Vector *) _localptrs[0])->data[1 - 1] = 100;
    print_vector(_localptrs[0]);
}

int main(int argc, char *argv[])
{
    void *_localptrs[1];

    _localptrs[0] = Vector_new((double[]) {1, 2, 3}, 3);
    bar(_localptrs[0]);
    COPY_ON_WRITE(_localptrs[0]);
    ((Vector *) _localptrs[0])->data[1 - 1] = 99;
    print_vector(_localptrs[0]);
    return 0;
}
