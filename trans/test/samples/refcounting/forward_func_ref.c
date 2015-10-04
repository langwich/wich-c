#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

String *f();
double g();

String *
f()
{
    ENTER();
    g();
    EXIT();
}

double
g()
{
    ENTER();
    f();
    EXIT();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    EXIT();
    return 0;
}
