#include <stdio.h>
#include "wich.h"
int
main(int argc, char *argv[])
{
    setup_error_handlers();
    String *hello;

    String *world;

    hello = String_new("hello");
    world = String_new("world");
    print_string(String_add(hello, world));
    return 0;
}
