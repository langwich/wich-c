#include <stdio.h>
#include "wich.h"
int
main(int argc, char *argv[])
{
    setup_error_handlers();
    String *s;

    PVector_ptr v;

    String *z;

    s = String_new("hello");
    v = Vector_new((double[]) {
                   1, 2, 3}, 3);
    z = String_add(s, String_from_vector(v));
    print_string(z);
    return 0;
}
