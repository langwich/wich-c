#include <stdio.h>
#include "wich.h"

extern String *f();
extern float g();

String *f()
{
	g();
}
float g()
{
	f();
}
int main(int argc, char *argv[])
{
	return 0;
}
