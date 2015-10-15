func f(a : []) {
	var b = "cat"
	{
		var c = "dog"
        {
            var d = "moo"
        }
	}
	{
		var b = "boo"   // hides outer b
		var c = "hoo"   // doesn't conflict with other c
	}
	var e = [7]
}
