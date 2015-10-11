#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
bool foo(int x);

bool
foo(int x)
{
    ENTER();
    {
        EXIT();
        return (x < 10);
    }
    EXIT();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    int x;

    bool y;

    x = 5;
    y = foo(x);
    if (y) {
        MARK();
        print_string(String_new("happy"));
        RELEASE();
    }
    else {
        MARK();
        print_string(String_new("sad"));
        RELEASE();
    }
    EXIT();
    return 0;
}