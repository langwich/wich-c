target triple = "x86_64-apple-macosx10.11.0"
%PVector = type { %heap_object, i32, i64, [0 x %_PVectorFatNode] }
%heap_object = type {}
%_PVectorFatNode = type { double, %_PVectorFatNodeElem* }
%_PVectorFatNodeElem = type { %heap_object, i32, double, %_PVectorFatNodeElem* }
%PVector_ptr = type { i32, %PVector* }

@pf.str = private unnamed_addr constant [7 x i8] c"%1.2f\0A\00", align 1
@pi.str = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1

declare i32 @printf(i8*, ...)

define i32 @fib(i32 %x) {
entry:
%x_ = alloca i32
store i32 %x, i32* %x_
%retval_ = alloca i32
%0 = load i32, i32% %x_
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
entry:
	%0 = add i32 0, 5
	%1 = call i32 (i32) @fib(i32 %0)
	%call = call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @pi.str, i64 0, i64 0), i32 %1)
	ret i32 0
}

@str = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1
