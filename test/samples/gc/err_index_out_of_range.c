#include <stdio.h>
#include "wich.h"
#include "gc.h"
int
main(int ____c, char *____v[])
{
    setup_error_handlers();
    gc_begin_func();
    VECTOR(x);
    x = Vector_new((double[]) {
                   1, 2, 3, 4}, 4);
    set_ith(x, 6 - 1, 5);
    print_vector(x);
    gc_end_func();
    gc();
    Heap_Info info = get_heap_info();

    if (info.live != 0)
        fprintf(stderr, "%d objects remain after collection\n", info.live);
    gc_shutdown();
    return 0;
}