#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    int x;

    x = 10;
    while ((x > 0)) {
        MARK();
        printf("%1.2f\n", (x + 1.0));
        x = (x - 1);
        RELEASE();
    }
    EXIT();
    return 0;
}
