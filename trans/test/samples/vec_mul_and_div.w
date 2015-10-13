func foo(x:int):[]{
	var y = [2,4,6]
	var z = (y / x)
	return z
}
var f = 5.00
var v = (foo(2) * f)
print (v)