#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
int
main(int ____c, char *____v[])
{
    setup_error_handlers();
    ENTER();
    int x;

    double y;

    x = 1;
    y = (3.14 + x);
    printf("%1.2f\n", y);
    EXIT();
    return 0;
}