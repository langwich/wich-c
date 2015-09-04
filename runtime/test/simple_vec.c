#include <stdio.h>
#include "wich.h"

/* translation of Wich code:

var x = [1, 2, 3]
var y = x
print(y)

 */

int main(int argc, char *argv[])
{
	Vector *x = Vector_new((double []){1,2,3}, 3);
	Vector *y = x;
	((heap_object *)y)->refs++;
	printf("%s\n", Vector_as_string(y));
}