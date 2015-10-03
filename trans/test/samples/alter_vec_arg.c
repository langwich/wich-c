#include <stdio.h>
#include "wich.h"

void bar(Vector * x);

void
bar(Vector * x)
{
    void *_localptrs[0];

    REF(x);

    COPY_ON_WRITE(x);
    x->data[1 - 1] = 100;
    print_vector(_localptrs[0]);
    deref(_localptrs);
}

int
main(int argc, char *argv[])
{
    void *_localptrs[1];

    _localptrs[0] = Vector_new((double[]) {
                               1, 2, 3}, 3);
    bar(_localptrs[0]);
    COPY_ON_WRITE(x);
    x->data[1 - 1] = 99;
    print_vector(_localptrs[0]);
    deref(_localptrs);
    return 0;
}
