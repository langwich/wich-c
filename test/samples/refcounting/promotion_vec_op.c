#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
int
main(int ____c, char *____v[])
{
    setup_error_handlers();
    ENTER();
    VECTOR(v);
    VECTOR(w);
    v = Vector_new((double[]) {
                   1.0, 2.0, 3.0}, 3);
    REF((void *)v.vector);
    v = Vector_add(v, 4);
    REF((void *)v.vector);
    w = Vector_add(Vector_from_int(100, (v).vector->length), v);
    REF((void *)w.vector);
    print_vector(v);
    print_vector(w);
    EXIT();
    return 0;
}