#include <stdio.h>
#include "wich.h"
#include "gc.h"
bool cmp(String * x);

bool
cmp(String * x)
{
    gc_begin_func();
    {
        gc_end_func();
        return String_eq(x, String_new("ca"));
    }
    gc_end_func();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    gc_begin_func();
    STRING(x);
    STRING(y);
    x = String_new("cat");
    y = String_new("dog");
    if (String_eq(x, y)) {
        print_string(String_new("x==y"));
    }
    if (String_neq(x, y)) {
        print_string(String_new("x!=y"));
    }
    printf("%d\n", cmp(x));
    gc_end_func();
    gc();
    Heap_Info info = get_heap_info();

    if (info.live != 0)
        fprintf(stderr, "%d objects remain after collection\n", info.live);
    gc_shutdown();
    return 0;
}
