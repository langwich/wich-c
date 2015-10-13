#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
bool f(String * s);

bool
f(String * s)
{
    ENTER();
    REF(s);
    if ((s <= String_new("cat"))) {
        MARK();
        {
            EXIT();
            return true;
        }
        RELEASE();
    }
    {
        EXIT();
        return false;
    }
    EXIT();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    STRING(s1);
    STRING(s2);
    s1 = String_new("");
    REF(s1);
    s2 = String_new("cat");
    REF(s2);
    if ((s1 > s2)) {
        MARK();
        RELEASE();
    }
    else {
        MARK();
        print_string(String_new("miaow"));
        RELEASE();
    }
    printf("%d\n", f(s2));
    EXIT();
    return 0;
}
