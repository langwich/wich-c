func bar(x:[]) {
	x[1] = 100.0
	print(x) // [100,2,3]
}

var x = [1,2,3]
bar(x)
x[1] = 99.0
print(x) // [99,2,3]
