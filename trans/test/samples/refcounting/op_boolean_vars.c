#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
bool foo(int x);

bool bar(int x);

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

bool
bar(int x)
{
    ENTER();
    if ((x < 1)) {
        MARK();
        {
            EXIT();
            return true;
        }
        RELEASE();
    }
    else {
        MARK();
        {
            EXIT();
            return false;
        }
        RELEASE();
    }
    EXIT();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    bool x;

    bool y;

    x = bar(5);
    y = foo(1);
    printf("%d\n", (x || y));
    EXIT();
    return 0;
}
