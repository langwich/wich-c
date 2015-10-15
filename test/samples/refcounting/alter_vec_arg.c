#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

void bar(Vector * x);

void
bar(Vector * x)
{
    ENTER();
    REF(x);
    COPY_ON_WRITE(x);
    x->data[1 - 1] = 100;
    print_vector(x);
    EXIT();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    VECTOR(x);
    x = Vector_new((double[]) {1, 2, 3}, 3);
    REF(x);
    bar(x);
    COPY_ON_WRITE(x);
    x->data[1 - 1] = 99;
    print_vector(x);
    EXIT();
    return 0;
}
