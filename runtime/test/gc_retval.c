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
	((heap_object *)x)->refs = 1;

	// code for return statement is split in two parts; part 1 indicates a ref will escape this context
	((heap_object *)x)->refs++; // count is now 2

	// end of scope code: drop ref count by 1 for all [] vars
	((heap_object *)x)->refs--; // count is now 1 and hence not free'd
	if ( x!=NULL && ((heap_object *)x)->refs==0 ) wich_free((heap_object *)x);

	// code for return statement is split in two parts; part 2
	return x;
}

int main(int argc, char *argv[])
{
	// var y = f()
	Vector *y = f();           // ref count is 1 for
	if ( y!=NULL && ((heap_object *)y)->refs==0 ) wich_free((heap_object *)y);

	// f(x)
	Vector *tmp = f();			// need to track return values for free'ing
	((heap_object *)tmp)->refs--; // tmp is not a lasting ref
	if ( tmp!=NULL && ((heap_object *)tmp)->refs==0 ) wich_free((heap_object *)tmp); // frees f() ret value here

	// end of global scope
	((heap_object *)y)->refs--;
	if ( y!=NULL && ((heap_object *)y)->refs==0 ) wich_free((heap_object *)y); // frees object from y=f() finally
}
