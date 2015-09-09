#include <stdio.h>
#include "wich.h"

/* translation of Wich code:

var x = [1, 2, 3]

func f(x : []) : [] {
	var y = x
	return y
}

var y = f(x)
f(x)
 */

Vector *f(Vector *x)
{
	// add ref counts for all vector args
	REF(x); // arg is 2nd ref to [1,2,3]; global x is first

	// body of function
	Vector *y = x;		    // another ref
	REF(y);

	// part 1 of return statement
	REF(y);

	// end of scope code: drop ref count by 1 for all [] vars; net ref count is 0
	DEREF(x);
	DEREF(y);

	return y;
}

int main(int argc, char *argv[])
{
	// var x = [1, 2, 3]
	Vector *x = Vector_new((double []){1,2,3}, 3);

	// var y = f(x)
	Vector *y = f(x);

	// f(x)
	Vector *tmp;
	tmp = f(x);			// need to track return values to free

	// end of global scope
	DEREF(x);
	DEREF(y);
	DEREF(tmp);
}
