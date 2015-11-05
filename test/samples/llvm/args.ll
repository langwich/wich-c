target triple = "x86_64-apple-macosx10.11.0"
%PVector = type { %heap_object, i32, i64, [0 x %_PVectorFatNode] }
%heap_object = type {}
%_PVectorFatNode = type { double, %_PVectorFatNodeElem* }
%_PVectorFatNodeElem = type { %heap_object, i32, double, %_PVectorFatNodeElem* }
%PVector_ptr = type { i32, %PVector* }

@pf.str = private unnamed_addr constant [7 x i8] c"%1.2f\0A\00", align 1

declare i32 @printf(i8*, ...)

define void @f(i32 %x, %PVector_ptr %v) {
entry:
	%x_ = alloca i32
	store i32 %x, i32* %x_
	%v_ = alloca %PVector_ptr
	store %PVector_ptr %v, %PVector_ptr* %v_

	br label %ret__
ret__:
	br label %ret_

ret_:
	ret void
}


define i32 @main(i32 %argc, i8** %argv) {
entry:
	%retval_ = alloca i32
	%argc_ = alloca i32
	%argv_ = alloca i8**
	store i32 0, i32* %retval_
	store i32 %argc, i32* %argc_
	store i8** %argv, i8*** %argv_

	br label %ret__
ret__:
	br label %ret_

ret_:
	%retval = load i32, i32* %retval_
	ret i32 %retval
}

