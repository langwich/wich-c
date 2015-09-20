#include "wich.h"

Vector *foo() {

	Vector *x = Vector_new((double[]){1,2,3,4,5}, 5);

	REF(x);

	DEREF(x);

	return x;
}

int main(int argc, char *argv[]) {

	Vector *x = foo();
	Vector *tmp1;

	print_vector(tmp1=foo());

	DEREF(x)
	DEREF(tmp1)

	return 0;
}