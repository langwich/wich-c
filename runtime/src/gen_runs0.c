#include <stdio.h>

int runs(int i)
{
	_builtin_
}

int main(int argc, char *argv[])
{
	// print how many runs of 0 bits of length n for a value of i=0.255?
	for (int i=0; i<255; i++)
	{
		printf("%d // i=%d\n", runs(i), i);
	}
}