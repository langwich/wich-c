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
	((heap_object *)x)->refs++; // arg is 2nd ref to [1,2,3]; global x is first

	// body of function
	Vector *y = x;		    // another ref
	((heap_object *)y)->refs++;

	// end of scope code: drop ref count by 1 for all [] vars; net ref count is 0
	((heap_object *)y)->refs--;
	((heap_object *)x)->refs--;
	// for ref counting GC, here is where we'd check for ref count == 0 to free()

	// TODO for ref counting GC: we need to handle return of refs to local vectors; see gc_retval.c

	return y;
}

int main(int argc, char *argv[])
{
	// var x = [1, 2, 3]
	Vector *x = Vector_new((double []){1,2,3}, 3);

	// var y = f(x)
	Vector *y = f(x);
	((heap_object *)y)->refs++; // adding another ref due to return value assignment

	// f(x)
	f(x);			// no need to track return values unless we add a ref

	// end of global scope
	((heap_object *)x)->refs--;
	((heap_object *)y)->refs--;
	// for ref counting GC, here is where we'd check for ref count == 0 to free()
}
