#include <stdio.h>
#include "wich.h"
bool f(String * s);

bool
f(String * s)
{
    if ((s <= String_new("cat"))) {
        return true;
    }
    return false;
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    String *s1;

    String *s2;

    s1 = String_new("");
    s2 = String_new("cat");
    if ((s1 > s2)) {
    }
    else {
        print_string(String_new("miaow"));
    }
    printf("%d\n", f(s2));
    return 0;
}