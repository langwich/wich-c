#include <stdio.h>
#include "wich.h"
void f();

void
f()
{
    String *x;

    x = String_new("cat");
    {
        String *y;

        String *z;

        y = String_new("dog");
        z = x;
    }
}

int
main(int argc, char *argv[])
{
	setup_error_handlers();
    f();
    return 0;
}
