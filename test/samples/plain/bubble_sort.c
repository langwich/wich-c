#include <stdio.h>
#include "wich.h"
PVector_ptr bubbleSort(PVector_ptr v);

PVector_ptr
bubbleSort(PVector_ptr v)
{
    int length;

    int i;

    int j;

    length = Vector_len(v);
    i = 1;
    j = 1;
    while ((i <= length)) {
        j = 1;
        while ((j <= (length - i))) {
            if ((ith(v, (j) - 1) > ith(v, ((j + 1)) - 1))) {
                double swap;

                swap = ith(v, (j) - 1);
                set_ith(v, j - 1, ith(v, ((j + 1)) - 1));
                set_ith(v, (j + 1) - 1, swap);
            }
            j = (j + 1);
        }
        i = (i + 1);
    }
    return v;
}

int
main(int ____c, char *____v[])
{
    setup_error_handlers();
    PVector_ptr x;

    x = Vector_new((double[]) {
                   100, 99, 4, 2.15, 2, 23, 3}, 7);
    print_vector(bubbleSort(PVector_copy(x)));
    return 0;
}
