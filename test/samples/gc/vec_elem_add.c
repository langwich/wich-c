#include <stdio.h>
#include "wich.h"
#include "gc.h"
int
main(int argc, char *argv[])
{
    setup_error_handlers();
    gc_begin_func();
    VECTOR(u);
    VECTOR(v);
    u = Vector_new((double[]) {
                   1, 2, 3}, 3);
    v = Vector_new((double[]) {
                   2, 3, 4}, 3);
    printf("%1.2f\n", (ith(u, (1) - 1) + ith(v, (3) - 1)));
    gc_end_func();
    gc();
    Heap_Info info = get_heap_info();

    if (info.live != 0)
        fprintf(stderr, "%d objects remain after collection\n", info.live);
    gc_shutdown();
    return 0;
}
