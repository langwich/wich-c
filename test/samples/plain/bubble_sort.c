#include <stdio.h>
#include "wich.h"
PVector_ptr bubbleSort(PVector_ptr vec);

PVector_ptr
bubbleSort(PVector_ptr vec)
{
    int length;

    PVector_ptr v;

    int i;

    int j;

    length = Vector_len(vec);
    v = PVector_copy(vec);
    i = 1;
    j = 1;
    while ((i <= length)) {
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
main(int argc, char *argv[])
{
    setup_error_handlers();
    PVector_ptr x;

    x = Vector_new((double[]) {
                   1, 4, 2, 3}, 4);
    print_vector(bubbleSort(PVector_copy(x)));
    return 0;
}
