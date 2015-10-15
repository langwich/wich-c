func f (x:int):float{
	var y = 1.0
	return (x+-y)
}

var z = f(2)
if(!z) {
	print ("z==0")
}
else {
	print ("z!=0")
}