#include <stdio.h>
#include "wich.h"

/* translation of Wich code:

func f() {
	var x = "cat" + "dog"
	print(x)
	print(x[1]+x[3]) // x[i] returns a String with one character in it
}

f()
 */

void f() {
	// var x = "cat" + "dog"
	String *tmp1;
	String *tmp2;
	String *x = String_add(tmp1=String_new("cat"), tmp2=String_new("dog"));

	// print(x)
	print_string(x);

	// print(x[1]+x[3])
	String *tmp3;
	String *tmp4;
	String *tmp5;
	print_string(
		   tmp5=String_add(
			   tmp3=String_from_char(x->str[(1)-1]),
			   tmp4=String_from_char(x->str[(3)-1])
		   )
	);

	// end of scope code; drop ref count by 1 for all [], string vars
	DEREF(x);
	DEREF(tmp1);
	DEREF(tmp2);
	DEREF(tmp3);
	DEREF(tmp4);
	DEREF(tmp5);
}

int main(int argc, char *argv[])
{
	f();
}
