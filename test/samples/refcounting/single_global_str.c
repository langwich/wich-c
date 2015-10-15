#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    STRING(x);
    x = String_new("Hello World!");
    REF(x);
    EXIT();
    return 0;
}
