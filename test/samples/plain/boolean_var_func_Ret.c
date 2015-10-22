#include <stdio.h>
#include "wich.h"
bool foo(int x);

bool
foo(int x)
{
    return (x < 10);
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
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
    return 0;
}
