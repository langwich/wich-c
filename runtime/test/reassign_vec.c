#include <stdio.h>
#include "wich.h"

/* translation of Wich code:

var x = [1, 2, 3]
x = [4,5]

 */

int main(int argc, char *argv[])
{
	// var x = [1, 2, 3]
	Vector *x = Vector_new((double []){1,2,3}, 3);
	((heap_object *)x)->refs++;

	// x = [4,5]
	if ( x!=NULL ) ((heap_object *)x)->refs--; // all assignments must technically drop any previous ref
	x = Vector_new((double []){4,5}, 2);
	((heap_object *)x)->refs++;

	// end of global scope
	((heap_object *)x)->refs--;
	// for ref counting GC, here is where we'd check for ref count == 0 to free()
}
