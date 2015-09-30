#include <stdio.h>
#include "wich.h"

int x;

int y;

int
main(int argc, char *argv[])
{
    x = 2;
    y = 1;
    if ((x > y)) {
        print_string(String_new("TRUE"));
    }
    else {
        print_string(String_new("FALSE"));
    }
    return 0;
}
