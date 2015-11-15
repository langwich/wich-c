#include <stdio.h>
#include "wich.h"
bool cmp(String * x);

bool
cmp(String * x)
{
    return String_eq(x, String_new("ca"));
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    String *x;

    String *y;

    x = String_new("cat");
    y = String_new("dog");
    if (String_eq(x, y)) {
        print_string(String_new("x==y"));
    }
    if (String_neq(x, y)) {
        print_string(String_new("x!=y"));
    }
    printf("%d\n", cmp(x));
    return 0;
}
