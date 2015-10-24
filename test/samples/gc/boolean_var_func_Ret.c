#include <stdio.h>
#include "wich.h"
#include "gc.h"
bool foo(int x);

bool
foo(int x)
{
    gc_begin_func();
    {
        gc_end_func();
        return (x < 10);
    }
    gc_end_func();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    gc_begin_func();
    int x;

    bool y;

    x = 5;
    y = foo(x);
    if (y) {
        print_string(String_new("happy"));
    }
    else {
        print_string(String_new("sad"));
    }
    gc_end_func();
    gc();
    Heap_Info info = get_heap_info();

    if (info.live != 0)
        fprintf(stderr, "%d objects remain after collection\n", info.live);
    gc_shutdown();
    return 0;
}
