#include <stdio.h>
#include "wich.h"
void f(Vector * a);

void
f(Vector * a)
{
    String *b;

    Vector *e;

    b = String_new("cat");
    {
        String *c;

        c = String_new("dog");
        {
            String *d;

            d = String_new("moo");
        }
    }
    {
        String *b;

        String *c;

        b = String_new("boo");
        c = String_new("hoo");
    }
    e = Vector_new((double[]) {
                   7}, 1);
}

int
main(int argc, char *argv[])
{
	setup_error_handlers();
    return 0;
}
