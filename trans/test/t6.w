func fib(x:int) : int {
	if ((x == 0) || (x == 1)) {
		return x
	}
	return fib(x-1) + fib(x-2)
}

print(fib(5))