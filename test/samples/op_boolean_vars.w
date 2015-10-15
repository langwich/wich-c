func foo(x:int):boolean {
	return (x<10)
}
func bar(x:int):boolean {
	if (x < 1) {
		return true
	}
	else {
		return false
	}
}
var x = bar(5)
var y = foo(1)

print(x||y)