func foo(x:int):boolean {
	return (x<10)
}
var x = 5
var y = foo(x)
if (y) {
	print ("happy")
}else{
	print ("sad")
}