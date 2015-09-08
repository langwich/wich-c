#include <stdio.h>
#include "wich.h"

/* translation of Wich code:

func f() : [] {
	var x = [1, 2, 3]
	return x
}

var y = f()
f()
 */

Vector *f()
{
	// var x = [1, 2, 3]
	Vector *x = Vector_new((double []){1,2,3}, 3);
	REF(x);

	// code for return statement is split in two parts; part 1 indicates a ref will escape this context
	REF(x); // count is now 2

	// end of scope code: drop ref count by 1 for all [], string vars
	DEREF(x);

	// code for return statement is split in two parts; part 2
	return x;
}

int main(int argc, char *argv[])
{
	// var y = f()
	Vector *y = f();                // ref count is 1 for return value

	// f(x)
	Vector *tmp;
	tmp = f();      	            // need to track return values for free'ing at end of scope

	// end of global scope
	DEREF(y);
	DEREF(tmp);
}
