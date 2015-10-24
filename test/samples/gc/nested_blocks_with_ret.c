#include <stdio.h>
#include "wich.h"
#include "gc.h"
int f(PVector_ptr a);

int
f(PVector_ptr a)
{
    ENTER();
    int x;

    STRING(b);
    VECTOR(e);
    x = 32;
    b = String_new("cat");
    {
        STRING(c);
        c = String_new("dog");
        {
            STRING(d);
            d = String_new("moo");
            {
                EXIT();
                return x;
            }
        }
    }
    {
        STRING(b);
        b = String_new("boo");
    }
    e = Vector_new((double[]) {
                   7}, 1);
    EXIT();
}

int
main(int argc, char *argv[])
{
    setup_error_handlers();
    ENTER();
    printf("%d\n", f(Vector_new((double[]) {
                                1}, 1)));
    EXIT();
    return 0;
}
