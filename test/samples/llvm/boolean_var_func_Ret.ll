target triple = "x86_64-apple-macosx10.11.0"
%PVector = type { %heap_object, i32, i64, [0 x %_PVectorFatNode] }
%heap_object = type {}
%_PVectorFatNode = type { double, %_PVectorFatNodeElem* }
%_PVectorFatNodeElem = type { %heap_object, i32, double, %_PVectorFatNodeElem* }
%PVector_ptr = type { i32, %PVector* }

@pf.str = private unnamed_addr constant [7 x i8] c"%1.2f\0A\00", align 1
@pi.str = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1

; Wich builtin functions
declare void @print_vector(%PVector_ptr)
declare void @set_ith(%PVector_ptr, i32, double)
declare %PVector_ptr @PVector_new(double*, i32)

define i1 @foo(i32 %x) {
entry:
	%retval_ptr = alloca i1
; <label>:0
	%0 = icmp eq i32 %x, 10
	store i1 %0, i1* %retval_ptr
	br label %label.ret
label.ret:
	%retval = load i1, i1* %retval_ptr
	ret i1 %retval
}

define i32 @main(i32 %argc, i8** %argv) {
; <label>:0
	; declare variable
	%1 = alloca i32
	%2 = alloca i1

	store i32 5, i32* %1
	%x = load i32, i32* %1

	%3 = call i1 (i32) @foo(i32 %x)
	store i1 %3, i1* %2
	%y = load i1, i1* %2

	; ...
	ret i32 0
}

declare i32 @printf(i8*, ...)

@str1 = private unnamed_addr constant [6 x i8] c"happy\00", align 1
@str2 = private unnamed_addr constant [4 x i8] c"sad\00", align 1