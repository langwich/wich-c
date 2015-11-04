target triple = "x86_64-apple-macosx10.11.0"
%PVector = type { %heap_object, i32, i64, [0 x %_PVectorFatNode] }
%heap_object = type {}
%_PVectorFatNode = type { double, %_PVectorFatNodeElem* }
%_PVectorFatNodeElem = type { %heap_object, i32, double, %_PVectorFatNodeElem* }
%PVector_ptr = type { i32, %PVector* }

define i32 @fib(i32 %x) {
; <label>:0
	%1 = icmp eq i32 %x, 0
	br i1 %1, label %4, label %2
; <label>:2
	%3 = icmp eq i32 %x, 1
	br i1 %3, label %4, label %5
; <label>:4
	ret i32 %x
; <label>:5
	%6 = sub i32 %x, 1
	%7 = call i32 (i32) @fib(i32 %6)
	%8 = sub i32 %x, 2
	%9 = call i32 (i32) @fib(i32 %8)
	%10 = add i32 %7, %9
	ret i32 %10
}

define i32 @main(i32 %argc, i8** %argv) {
; <label>:0
	%1 = call i32 (i32) @fib(i32 5)
	call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @str, i32 0, i32 0), i32 %1)
	ret i32 0
}

; declare system function(s)
declare i32 @printf(i8*, ...)

@str = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1