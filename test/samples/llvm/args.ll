target triple = "x86_64-apple-macosx10.11.0"
%PVector = type { %heap_object, i32, i64, [0 x %_PVectorFatNode] }
%heap_object = type {}
%_PVectorFatNode = type { double, %_PVectorFatNodeElem* }
%_PVectorFatNodeElem = type { %heap_object, i32, double, %_PVectorFatNodeElem* }
%PVector_ptr = type { i32, %PVector* }

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
	%retval_ptr = alloca i32
	%argc_ptr = alloca i32
	%argv_ptr = alloca i8**
	store i32 0, i32* %retval_ptr
	store i32 %argc, i32* %argc_ptr
	store i8** %argv, i8*** %argv_ptr

	br label %ret__
ret__:
	br label %ret_

ret_:
	%retval = load i32, i32* %retval_ptr
	ret i32 %retval
}
