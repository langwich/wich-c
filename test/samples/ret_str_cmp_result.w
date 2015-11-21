func str_gt(s1:string, t:string):boolean {
	return s1 > t
}

func gt_msg(s:string, t:string) {
	print(s + " is greater than " + t)
}

func le_msg(s:string, t:string) {
	print(s + " is less than or equal to " + t)
}

var s1 = "hellp"
var s2 = "aello"
var t = "hello"

var s1t = str_gt(s1, t)
if (s1t) {
	gt_msg(s1,t)
}
else {
	le_msg(s1,t)
}

var s2t = str_gt(s2,t)
if (s2t) {
	gt_msg(s2,t)
}
else {
	le_msg(s2,t)
}