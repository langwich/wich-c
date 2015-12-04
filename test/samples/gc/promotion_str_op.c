#include <stdio.h>
#include "wich.h"
#include "gc.h"
int
main(int ____c, char *____v[])
{
    setup_error_handlers();
    gc_begin_func();
    STRING(s1);
    STRING(s2);
    STRING(s3);
    STRING(s4);
    STRING(s5);
    s1 = String_new("abc");
    s2 = String_add(s1, String_new("xyz"));
    s3 = String_add(s1, String_from_int(100));
    s4 = String_add(s1, String_from_float(3.14));
    s5 = String_add(s1, String_from_vector(Vector_new((double[]) {
                                                      1, 2, 3}, 3)));
    print_string(s1);
    print_string(s2);
    print_string(s3);
    print_string(s4);
    print_string(s5);
    gc_end_func();
    gc();
    Heap_Info info = get_heap_info();

    if (info.live != 0)
        fprintf(stderr, "%d objects remain after collection\n", info.live);
    gc_shutdown();
    return 0;
}