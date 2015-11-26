#include <stdio.h>
#include "wich.h"
#include "gc.h"
int
main(int ____c, char *____v[])
{
    setup_error_handlers();
    gc_begin_func();
    STRING(a);
    int b;

    a = String_new("hello");
    b = Vector_len(Vector_new((double[]) {
                              1, 2, 3}, 3));
    printf("%d\n", ((String_len(a) + String_len(String_new("world"))) + b));
    gc_end_func();
    gc();
    Heap_Info info = get_heap_info();

    if (info.live != 0)
        fprintf(stderr, "%d objects remain after collection\n", info.live);
    gc_shutdown();
    return 0;
}
