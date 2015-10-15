#include <stdio.h>
#include "wich.h"
Vector *foo(int x);

Vector *
foo(int x)
{
    Vector *y;

    Vector *z;

    y = Vector_new((double[]) {
                   2, 4, 6}, 3);
    z = Vector_div(y, Vector_from_int(x, y));
    return z;
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    double f;

    Vector *v;

    f = 5.00;
    v = Vector_mul(foo(2), Vector_from_float(f, foo(2)));
    print_vector(v);
    return 0;
}
