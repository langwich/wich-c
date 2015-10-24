#include <stdio.h>
#include "wich.h"
#include "gc.h"
int fib(int x);

int
fib(int x)
{
    gc_begin_func();
    if (((x == 0) || (x == 1))) {
        {
            gc_end_func();
            return x;
        }
    }
    {
        gc_end_func();
        return (fib((x - 1)) + fib((x - 2)));
    }
    gc_end_func();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    gc_begin_func();
    printf("%d\n", fib(5));
    gc_end_func();
    gc();
    Heap_Info info = get_heap_info();

    if (info.live != 0)
        fprintf(stderr, "%d objects remain after collection\n", info.live);
    gc_shutdown();
    return 0;
}
