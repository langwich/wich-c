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
%retval_ = alloca i32
%argc_ = alloca i32
%argv_ = alloca i8**
store i32 0, i32* %retval_
store i32 %argc, i32* %argc_
store i8** %argv, i8*** %argv_

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
%4 = load i32, i32* %x
%5 = sitofp i32 %4 to double
%6 = fadd double 1.0, 0.00
%7 = fadd double %5, %6
%call = call i32 (i8*, ...) @printf(i8* getelementptr ([7 x i8], [7 x i8]* @pf.str, i64 0, i64 0), double %7)
%8 = load i32, i32* %x
%9 = add i32 1, 0
%10 = sub i32 %8, %9
store i32 %10, i32* %x
br label %while.block_entry_0
while.block_exit_0:

br label %ret__
ret__:
br label %ret_

ret_:
%retval = load i32, i32* %retval_
ret i32 %retval
}
