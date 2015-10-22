#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
bool cmp(String * x);

bool
cmp(String * x)
{
    ENTER();
    REF((void *)x);
    {
        EXIT();
        return (x == String_new("ca"));
    }
    EXIT();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    STRING(x);
    STRING(y);
    x = String_new("cat");
    REF((void *)x);
    y = String_new("dog");
    REF((void *)y);
    if ((x == y)) {
        MARK();
        print_string(String_new("x==y"));
        RELEASE();
    }
    if ((x != y)) {
        MARK();
        print_string(String_new("x!=y"));
        RELEASE();
    }
    printf("%d\n", cmp(x));
    EXIT();
    return 0;
}
