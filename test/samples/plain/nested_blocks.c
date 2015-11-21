#include <stdio.h>
#include "wich.h"

void f(PVector_ptr a);

void f(PVector_ptr a)
{
    String * b;
    PVector_ptr e;
    b = String_new("cat");
    {
        String * c;
        c = String_new("dog");
        {
            String * d;
            d = String_new("moo");
        }
    }
    {
        String * b;
        String * c;
        b = String_new("boo");
        c = String_new("hoo");
    }
    e = Vector_new((double []){7}, 1);

}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
	return 0;
}

