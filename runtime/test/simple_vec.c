#include <stdio.h>
#include "wich.h"

/* translation of Wich code:

var x = [1, 2, 3]
var y = x
print(y)

 */

int main(int argc, char *argv[])
{
	// var x = [1, 2, 3]
	Vector *x = Vector_new((double []){1,2,3}, 3);
	((heap_object *)x)->refs++;

	// var y = x
	Vector *y = x;
	((heap_object *)y)->refs++;

	// print(y)
	printf("%s\n", Vector_as_string(y));

	// end of global scope
	((heap_object *)x)->refs--;
	((heap_object *)y)->refs--;
	// for ref counting GC, here is where we'd check for ref count == 0 to free()
}
