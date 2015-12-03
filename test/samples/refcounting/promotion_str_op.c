#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
int
main(int ____c, char *____v[])
{
    setup_error_handlers();
    ENTER();
    STRING(s1);
    STRING(s2);
    STRING(s3);
    STRING(s4);
    STRING(s5);
    s1 = String_new("abc");
    REF((void *)s1);
    s2 = String_add(s1, String_new("xyz"));
    REF((void *)s2);
    s3 = String_add(s1, String_from_int(100));
    REF((void *)s3);
    s4 = String_add(s1, String_from_float(3.14));
    REF((void *)s4);
    s5 = String_add(s1, String_from_vector(Vector_new((double[]) {
                                                      1, 2, 3}, 3)));
    REF((void *)s5);
    print_string(s1);
    print_string(s2);
    print_string(s3);
    print_string(s4);
    print_string(s5);
    EXIT();
    return 0;
}