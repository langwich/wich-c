#include <stdio.h>
#include "wich.h"
#include "gc.h"
int
main(int ____c, char *____v[])
{
    setup_error_handlers();
    gc_begin_func();
    VECTOR(v);
    VECTOR(w);
    v = Vector_new((double[]) {
                   1.0, 2.0, 3.0}, 3);
    v = Vector_add(v, Vector_from_int(4, (v).vector->length));
    w = Vector_add(Vector_from_int(100, (v).vector->length), v);
    print_vector(v);
    print_vector(w);
    gc_end_func();
    gc();
    Heap_Info info = get_heap_info();

    if (info.live != 0)
        fprintf(stderr, "%d objects remain after collection\n", info.live);
    gc_shutdown();
    return 0;
}