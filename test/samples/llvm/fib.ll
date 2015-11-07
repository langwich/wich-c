target triple = "x86_64-apple-macosx10.11.0"
%struct.PVector_ptr = type { i32, %struct.PVector* }
%struct.PVector = type { %struct.heap_object, i32, i64, [0 x %struct._PVectorFatNode] }
%struct.heap_object = type {}
%struct._PVectorFatNode = type { double, %struct._PVectorFatNodeElem* }
%struct._PVectorFatNodeElem = type { %struct.heap_object, i32, double, %struct._PVectorFatNodeElem* }
%PVector = type { %heap_object, i32, i64, [0 x %_PVectorFatNode] }

declare { i32, %struct.PVector* } @PVector_init(double, i64) #1

declare void @print_pvector(i32, %struct.PVector*) #1

declare { i32, %struct.PVector* } @PVector_new(double*, i64) #1

declare void @set_ith(i32, %struct.PVector*, i32, double) #1

declare i32 @printf(i8*, ...) #1

declare double @ith(i32, %struct.PVector*, i32) #1

declare i8* @PVector_as_string(i32, %struct.PVector*) #1

; Function Attrs: inlinehint nounwind ssp uwtable
define internal { i32, %struct.PVector* } @PVector_copy(i32 %v.coerce0, %struct.PVector* %v.coerce1) #2 {
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

define i32 @fib(i32 %x) {
entry:
%x_ = alloca i32
store i32 %x, i32* %x_
%retval_ = alloca i32
%0 = load i32, i32* %x_
%1 = add i32 0, 0
%2 = icmp eq i32 %0, %1
%3 = load i32, i32* %x_
%4 = add i32 1, 0
%5 = icmp eq i32 %3, %4
%6 = or i1 %2, %5
br i1 %6, label %if.block_true_0, label %if.block_false_0
if.block_true_0:
%7 = load i32, i32* %x_
store i32 %7, i32* %retval_
br label %ret_
return.exit_0:
br label %if.block_exit_0
if.block_false_0:
br label %if.block_exit_0
if.block_exit_0:
%8 = load i32, i32* %x_
%9 = add i32 1, 0
%10 = sub i32 %8, %9
%11 = call i32 (i32) @fib(i32 %10)
%12 = load i32, i32* %x_
%13 = add i32 2, 0
%14 = sub i32 %12, %13
%15 = call i32 (i32) @fib(i32 %14)
%16 = add i32 %11, %15
store i32 %16, i32* %retval_
br label %ret_
return.exit_1:

br label %ret__
ret__:
br label %ret_

ret_:
%retval = load i32, i32* %retval_
ret i32 %retval
}


define i32 @main(i32 %argc, i8** %argv) {
entry:
%retval_ = alloca i32
%argc_ = alloca i32
%argv_ = alloca i8**
store i32 0, i32* %retval_
store i32 %argc, i32* %argc_
store i8** %argv, i8*** %argv_
%0 = add i32 5, 0
%1 = call i32 (i32) @fib(i32 %0)
%pi_0 = call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @pi.str, i64 0, i64 0), i32 %1)
br label %ret__
ret__:
br label %ret_
ret_:
%retval = load i32, i32* %retval_
ret i32 %retval
}
