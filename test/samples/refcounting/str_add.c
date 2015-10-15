#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    STRING(hello);
    STRING(world);
    hello = String_new("hello");
    REF(hello);
    world = String_new("world");
    REF(world);
    print_string(String_add(hello, world));
    EXIT();
    return 0;
}