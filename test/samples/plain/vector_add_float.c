#include <stdio.h>
#include "wich.h"
PVector_ptr f(double x);

PVector_ptr
f(double x)
{
    PVector_ptr y;

    PVector_ptr z;

    y = Vector_new((double[]) {
                   1, 2, 3}, 3);
    z = Vector_add(y, Vector_from_float(x, (y).vector->length));
    return z;
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    print_vector(f(4.00));
    return 0;
}
