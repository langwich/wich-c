#include <stdio.h>
#include "wich.h"
#include "gc.h"
PVector_ptr bubbleSort(PVector_ptr v);

PVector_ptr
bubbleSort(PVector_ptr v)
{
    gc_begin_func();
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
    {
        gc_end_func();
        return v;
    }
    gc_end_func();
}

int
main(int ____c, char *____v[])
{
    setup_error_handlers();
    gc_begin_func();
    VECTOR(x);
    x = Vector_new((double[]) {
                   100, 99, 4, 2.15, 2, 23, 3}, 7);
    print_vector(bubbleSort(PVector_copy(x)));
    gc_end_func();
    gc();
    Heap_Info info = get_heap_info();

    if (info.live != 0)
        fprintf(stderr, "%d objects remain after collection\n", info.live);
    gc_shutdown();
    return 0;
}
