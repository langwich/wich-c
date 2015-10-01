func f(a : []) : int {
	var x = 32
	var b = "cat"
	{
		var c = "dog"
        {
            var d = "moo"
            return x	// exit function after cleanup of a,b,c,d
        }
	}
	{
		var b = "boo"   // hides outer b
	}
	var e = [7]
}

print( f([1]) )
