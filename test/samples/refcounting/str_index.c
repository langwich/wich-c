#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
void f();

void
f()
{
    ENTER();
    STRING(x);
    x = String_add(String_new("cat"), String_new("dog"));
    REF((void *)x);
    print_string(x);
    print_string(String_add(String_from_char(x->str[(1) - 1]), String_from_char(x->str[(3) - 1])));
    EXIT();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    f();
    EXIT();
    return 0;
}
