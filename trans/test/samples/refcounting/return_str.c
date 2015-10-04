#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
String *becomeSuper(String * name);

String *
becomeSuper(String * name)
{
    ENTER();
    REF(name);
    {
        EXIT();
        return String_add(String_new("super"), name);
    }
    EXIT();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    print_string(becomeSuper(String_new("man")));
    print_string(becomeSuper(String_new("duper")));
    EXIT();
    return 0;
}
