#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
void bar(PVector_ptr x);

void
bar(PVector_ptr x)
{
    ENTER();
    set_ith(x, 1 - 1, 100.0);
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
    bar(x);
    set_ith(x, 1 - 1, 99.0);
    print_vector(x);
    EXIT();
    return 0;
}
