#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    STRING(a);
    int b;

    a = String_new("hello");
    REF((void *)a);
    b = Vector_len(Vector_new((double[]) {
                              1, 2, 3}, 3));
    printf("%d\n", ((String_len(a) + String_len(String_new("world"))) + b));
    EXIT();
    return 0;
}