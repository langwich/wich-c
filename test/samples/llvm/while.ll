target triple = "x86_64-apple-macosx10.11.0"
%struct.PVector_ptr = type { i32, %struct.PVector* }
%struct.PVector = type { %struct.heap_object, i32, i64, [0 x %struct._PVectorFatNode] }
%struct.heap_object = type {}
%struct._PVectorFatNode = type { double, %struct._PVectorFatNodeElem* }
%struct._PVectorFatNodeElem = type { %struct.heap_object, i32, double, %struct._PVectorFatNodeElem* }

declare %struct.PVector_ptr @PVector_init(double, i64)

declare void @print_pvector(%struct.PVector_ptr)

declare %struct.PVector_ptr @PVector_new(double*, i64)

declare void @set_ith(%struct.PVector_ptr, i32, double)

declare double @ith(%struct.PVector_ptr, i32)

declare i8* @PVector_as_string(%struct.PVector_ptr)

; Function Attrs: inlinehint nounwind ssp uwtable
define internal { i32, %struct.PVector* } @PVector_copy(i32 %v.coerce0, %struct.PVector* %v.coerce1) {
%1 = alloca %struct.PVector_ptr, align 8
%v = alloca %struct.PVector_ptr, align 8
%2 = bitcast %struct.PVector_ptr* %v to { i32, %struct.PVector* }*
%3 = getelementptr inbounds { i32, %struct.PVector* }, { i32, %struct.PVector* }* %2, i32 0, i32 0
store i32 %v.coerce0, i32* %3, align 8
%4 = getelementptr inbounds { i32, %struct.PVector* }, { i32, %struct.PVector* }* %2, i32 0, i32 1
store %struct.PVector* %v.coerce1, %struct.PVector** %4, align 8
%5 = getelementptr inbounds %struct.PVector_ptr, %struct.PVector_ptr* %1, i32 0, i32 0
%6 = getelementptr inbounds %struct.PVector_ptr, %struct.PVector_ptr* %v, i32 0, i32 1
%7 = load %struct.PVector*, %struct.PVector** %6, align 8
%8 = getelementptr inbounds %struct.PVector, %struct.PVector* %7, i32 0, i32 1
%9 = load i32, i32* %8, align 8
%10 = add nsw i32 %9, 1
store i32 %10, i32* %8, align 8
store i32 %10, i32* %5, align 8
%11 = getelementptr inbounds %struct.PVector_ptr, %struct.PVector_ptr* %1, i32 0, i32 1
%12 = getelementptr inbounds %struct.PVector_ptr, %struct.PVector_ptr* %v, i32 0, i32 1
%13 = load %struct.PVector*, %struct.PVector** %12, align 8
store %struct.PVector* %13, %struct.PVector** %11, align 8
%14 = bitcast %struct.PVector_ptr* %1 to { i32, %struct.PVector* }*
%15 = load { i32, %struct.PVector* }, { i32, %struct.PVector* }* %14, align 8
ret { i32, %struct.PVector* } %15
}

@pf.str = private unnamed_addr constant [7 x i8] c"%1.2f\0A\00", align 1
@pi.str = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1

declare i32 @printf(i8*, ...)

define i32 @main(i32 %argc, i8** %argv) {
entry:
%retval_ = alloca i32
%argc_ = alloca i32
%argv_ = alloca i8**
store i32 0, i32* %retval_
store i32 %argc, i32* %argc_
store i8** %argv, i8*** %argv_

%x_ = alloca i32
%0 = add i32 10, 0
store i32 %0, i32* %x_

br label %while.block_entry_0
while.block_entry_0:
%1 = load i32, i32* %x_
%2 = add i32 0, 0
%3 = icmp sgt i32 %1, %2
br i1 %3, label %while.block_body_0, label %while.block_exit_0
while.block_body_0:
%4 = load i32, i32* %x_
%promo0 = sitofp i32 %4 to double
%5 = fadd double 1.0, 0.00
%6 = fadd double %promo0, %5
%pf_0 = call i32 (i8*, ...) @printf(i8* getelementptr ([7 x i8], [7 x i8]* @pf.str, i64 0, i64 0), double %6)
%7 = load i32, i32* %x_
%8 = add i32 1, 0
%9 = sub i32 %7, %8
store i32 %9, i32* %x_
br label %while.block_entry_0
while.block_exit_0:

br label %ret__
ret__:
br label %ret_

ret_:
%retval = load i32, i32* %retval_
ret i32 %retval
}
