#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
int f();

int
f()
{
    ENTER();
    EXIT();
}

int
main(int ____c, char *____v[])
{
    setup_error_handlers();
    ENTER();
    printf("%d\n", f());
    EXIT();
    return 0;
}