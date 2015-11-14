#include <stdio.h>
#include "wich.h"
int
main(int argc, char *argv[])
{
    setup_error_handlers();
    PVector_ptr u;

    PVector_ptr v;

    u = Vector_new((double[]) {
                   1, 2, 3}, 3);
    v = Vector_new((double[]) {
                   2, 3, 4}, 3);
    printf("%1.2f\n", (ith(u, (1) - 1) + ith(v, (3) - 1)));
    return 0;
}
