#include <stdio.h>
#include "wich.h"
int f(Vector * a);

int
f(Vector * a)
{
    int x;

    String *b;

    Vector *e;

    x = 32;
    b = String_new("cat");
    {
        String *c;

        c = String_new("dog");
        {
            String *d;

            d = String_new("moo");
            return x;
        }
    }
    {
        String *b;

        b = String_new("boo");
    }
    e = Vector_new((double[]) {
                   7}, 1);
}

int
main(int argc, char *argv[])
{
	setup_error_handlers();
    printf("%d\n", f(Vector_new((double[]) {
                                1}, 1)));
    return 0;
}
