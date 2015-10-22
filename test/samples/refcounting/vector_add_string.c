#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    STRING(s);
    VECTOR(v);
    STRING(z);
    s = String_new("hello");
    REF(s);
    v = Vector_new((double[]) {
                   1, 2, 3}, 3);
    REF(v);
    z = String_add(s, String_from_vector(v));
    REF(z);
    print_string(z);
    EXIT();
    return 0;
}
