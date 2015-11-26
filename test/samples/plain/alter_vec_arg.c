#include <stdio.h>
#include "wich.h"
void bar(PVector_ptr x);

void
bar(PVector_ptr x)
{
    set_ith(x, 1 - 1, 100);
    print_vector(x);
}

int
main(int ____c, char *____v[])
{
    setup_error_handlers();
    PVector_ptr x;

    x = Vector_new((double[]) {
                   1, 2, 3}, 3);
    bar(PVector_copy(x));
    set_ith(x, 2 - 1, 99);
    print_vector(x);
    return 0;
}
