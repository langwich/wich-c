#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
void bar(PVector_ptr x);

void
bar(PVector_ptr x)
{
    ENTER();
    REF((void *)x.vector);
    set_ith(x, 1 - 1, 100);
    print_vector(x);
    EXIT();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    VECTOR(x);
    x = Vector_new((double[]) { 1, 2, 3}, 3);
    REF((void *)x.vector);
    bar(x);
    set_ith(x, 1 - 1, 99);
    print_vector(x);
    EXIT();
    return 0;
}
