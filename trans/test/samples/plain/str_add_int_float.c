#include <stdio.h>
#include "wich.h"
int
main(int argc, char *argv[])
{
    setup_error_handlers();
    String *s;

    int i;

    double f;

    String *r;

    s = String_new("hello");
    i = 1;
    f = 1.00;
    r = String_add(String_new("world"), String_from_float(f));
    print_string(String_add(s, String_from_int(i)));
    print_string(r);
    return 0;
}