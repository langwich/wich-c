target triple = "x86_64-apple-macosx10.11.0"
%PVector = type { %heap_object, i32, i64, [0 x %_PVectorFatNode] }
%heap_object = type {}
%_PVectorFatNode = type { double, %_PVectorFatNodeElem* }
%_PVectorFatNodeElem = type { %heap_object, i32, double, %_PVectorFatNodeElem* }
%PVector_ptr = type { i32, %PVector* }

@pf.str = private unnamed_addr constant [7 x i8] c"%1.2f\0A\00", align 1

declare i32 @printf(i8*, ...)

define i32 @main(i32 %argc, i8** %argv) {
entry:
	%retval = alloca i32
	%argc = alloca i32
	%argv = alloca i8**
	store i32 0, i32* %retval
	store i32 %argc, i32* %argc
	store i8** %argv, i8*** %argv

	%x = alloca i32
	%0 = add i32 10, 0
	store i32 %0, i32* %x

	br label %while.block_entry_0
while.block_entry_0:
	%1 = load i32, i32* %x
	%2 = add i32 0, 0
	%3 = icmp sgt i32 %1, %2
	br i1 %3, label %while.block_body_0, label %while.block_exit_0
while.block_body_0:
	%conv_x = sitofp i32 %x to double
	%add = fadd double %conv_x, 1.000000e+00
	%call = tail call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([7 x i8], [7 x i8]* @.str, i64 0, i64 0), double %add)
	%sub = add i32 %x, -1
	%cmp = icmp sgt i32 %x, 1
br label %while.block_entry_0
while.block_exit_0:

	br label %ret_

	br label %ret__
ret__:
	br label %ret_

ret_:
	%retval_ = load i32, i32* %retval
	ret i32 %retval
}
