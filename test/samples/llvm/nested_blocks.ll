target triple = "x86_64-apple-macosx10.11.0"
; ///////// ///////// D A T A  S T R U C T U R E S ///////// /////////
%struct.__sFILE = type { i8*, i32, i32, i16, i16, %struct.__sbuf, i32, i8*, i32 (i8*)*, i32 (i8*, i8*, i32)*, i64 (i8*, i64, i32)*, i32 (i8*, i8*, i32)*, %struct.__sbuf, %struct.__sFILEX*, i32, [3 x i8], [1 x i8], %struct.__sbuf, i32, i64 }
%struct.__sFILEX = type opaque
%struct.__sbuf = type { i8*, i32 }
%struct.PVector_ptr = type { i32, %struct.PVector* }
%struct.PVector = type { %struct.heap_object, i32, i64, [0 x %struct._PVectorFatNode] }
%struct.heap_object = type {}
%struct._PVectorFatNode = type { double, %struct._PVectorFatNodeElem* }
%struct._PVectorFatNodeElem = type { %struct.heap_object, i32, double, %struct._PVectorFatNodeElem* }
%struct.string = type { %struct.heap_object, i64, [0 x i8] }
declare %struct.PVector_ptr @PVector_init(double, i64)
declare void @print_pvector(%struct.PVector_ptr)
declare %struct.PVector_ptr @PVector_new(double*, i64)
declare void @set_ith(%struct.PVector_ptr, i32, double)
declare double @ith(%struct.PVector_ptr, i32)
declare i8* @PVector_as_string(%struct.PVector_ptr)
; ///////// ///////// W I C H  R U N T I M E  F U N C T I O N S ///////// /////////
declare %struct.PVector_ptr @Vector_empty(i64)
declare %struct.PVector_ptr @Vector_copy(%struct.PVector_ptr)
declare %struct.PVector_ptr @Vector_new(double*, i64)
declare %struct.PVector_ptr @Vector_from_int(i32, i64)
declare %struct.PVector_ptr @Vector_from_float(double, i64)
declare %struct.PVector_ptr @Vector_add(%struct.PVector_ptr, %struct.PVector_ptr)
declare %struct.PVector_ptr @Vector_sub(%struct.PVector_ptr, %struct.PVector_ptr)
declare %struct.PVector_ptr @Vector_mul(%struct.PVector_ptr, %struct.PVector_ptr)
declare %struct.PVector_ptr @Vector_div(%struct.PVector_ptr, %struct.PVector_ptr)
declare void @print_vector(%struct.PVector_ptr)
declare %struct.string* @String_new(i8*)
declare %struct.string* @String_from_char(i8 signext)
declare %struct.string* @String_add(%struct.string*, %struct.string*)
declare %struct.string* @String_from_vector(%struct.PVector_ptr)
declare %struct.string* @String_from_int(i32)
declare %struct.string* @String_from_float(float)
declare void @print_string(%struct.string*)
declare zeroext i1 @String_eq(%struct.string*, %struct.string*)
declare zeroext i1 @String_neq(%struct.string*, %struct.string*)
declare zeroext i1 @String_gt(%struct.string*, %struct.string*)
declare zeroext i1 @String_ge(%struct.string*, %struct.string*)
declare zeroext i1 @String_lt(%struct.string*, %struct.string*)
declare zeroext i1 @String_le(%struct.string*, %struct.string*)
declare void (i32)* @signal(i32, void (i32)*)
; ///////// ///////// S Y S T E M  F U N C T I O N S ///////// /////////
declare i32 @fprintf(%struct.__sFILE*, i8*, ...)
declare void @exit(i32)
declare i32 @printf(i8*, ...)
; ///////// ///////// I N L I N E  F U N C T I O N S ///////// /////////
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
define internal void @setup_error_handlers() {
%1 = call void (i32)* @signal(i32 11, void (i32)* @handle_sys_errors)
%2 = call void (i32)* @signal(i32 10, void (i32)* @handle_sys_errors)
ret void
}
define internal void @handle_sys_errors(i32 %errno) {
%1 = alloca i32, align 4
%signame = alloca i8*, align 8
store i32 %errno, i32* %1, align 4
store i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.str.2, i32 0, i32 0), i8** %signame, align 8
%2 = load i32, i32* %1, align 4
%3 = icmp eq i32 %2, 11
br i1 %3, label %4, label %5
; label:4                                       ; preds = %0
store i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.str.3, i32 0, i32 0), i8** %signame, align 8
br label %10
; label:5                                       ; preds = %0
%6 = load i32, i32* %1, align 4
%7 = icmp eq i32 %6, 10
br i1 %7, label %8, label %9
; label:8                                       ; preds = %5
store i8* getelementptr inbounds ([7 x i8], [7 x i8]* @.str.4, i32 0, i32 0), i8** %signame, align 8
br label %9
; label:9                                       ; preds = %8, %5
br label %10
; label:10                                      ; preds = %9, %4
%11 = load %struct.__sFILE*, %struct.__sFILE** @__stderrp, align 8
%12 = load i8*, i8** %signame, align 8
%13 = load i32, i32* %1, align 4
%14 = call i32 (%struct.__sFILE*, i8*, ...) @fprintf(%struct.__sFILE* %11, i8* getelementptr inbounds ([34 x i8], [34 x i8]* @.str.5, i32 0, i32 0), i8* %12, i32 %13)
%15 = load i32, i32* %1, align 4
call void @exit(i32 %15) #4
unreachable
                                              ; No predecessors!
ret void
}
; ///////// ///////// C O N S T A N T S ///////// /////////
@pf.str = private unnamed_addr constant [7 x i8] c"%1.2f\0A\00", align 1
@pi.str = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1
@.str.2 = private unnamed_addr constant [8 x i8] c"UNKNOWN\00", align 1
@.str.3 = private unnamed_addr constant [8 x i8] c"SIGSEGV\00", align 1
@.str.4 = private unnamed_addr constant [7 x i8] c"SIGBUS\00", align 1
@__stderrp = external global %struct.__sFILE*, align 8
@.str.5 = private unnamed_addr constant [34 x i8] c"Wich is confused; signal %s (%d)\0A\00", align 1
; ///////// ///////// G E N E R A T E D  C O D E ///////// /////////
define void @f(%struct.PVector_ptr %a0) {
entry:
%a0_ = alloca %struct.PVector_ptr
store %struct.PVector_ptr %a0, %struct.PVector_ptr* %a0_
%b0_ = alloca %struct.string*
%e0_ = alloca %struct.PVector_ptr
%sl_0 = getelementptr [4 x i8], [4 x i8]* @sl.str0, i32 0, i32 0
%0 = call %struct.string* (i8*) @String_new(i8* %sl_0)
store %struct.string* %0, %struct.string** %b0_
%c0_ = alloca %struct.string*
%sl_1 = getelementptr [4 x i8], [4 x i8]* @sl.str1, i32 0, i32 0
%1 = call %struct.string* (i8*) @String_new(i8* %sl_1)
store %struct.string* %1, %struct.string** %c0_
%d0_ = alloca %struct.string*
%sl_2 = getelementptr [4 x i8], [4 x i8]* @sl.str2, i32 0, i32 0
%2 = call %struct.string* (i8*) @String_new(i8* %sl_2)
store %struct.string* %2, %struct.string** %d0_


%b1_ = alloca %struct.string*
%c1_ = alloca %struct.string*
%sl_3 = getelementptr [4 x i8], [4 x i8]* @sl.str3, i32 0, i32 0
%3 = call %struct.string* (i8*) @String_new(i8* %sl_3)
store %struct.string* %3, %struct.string** %b1_
%sl_4 = getelementptr [4 x i8], [4 x i8]* @sl.str4, i32 0, i32 0
%4 = call %struct.string* (i8*) @String_new(i8* %sl_4)
store %struct.string* %4, %struct.string** %c1_
%5 = alloca [1 x double]
%promo0_ = getelementptr [1 x double], [1 x double]* %5, i64 0, i64 0
%6 = add i32 7, 0
%promo0 = sitofp i32 %6 to double
store double %promo0, double* %promo0_
%vec_ptr_7 = getelementptr [1 x double], [1 x double]* %5, i64 0, i64 0
%7 = call %struct.PVector_ptr @PVector_new(double* %vec_ptr_7, i64 1)
store %struct.PVector_ptr %7, %struct.PVector_ptr* %e0_
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
call void () @setup_error_handlers()
br label %ret__
ret__:
br label %ret_
ret_:
%retval = load i32, i32* %retval_
ret i32 %retval
}


@sl.str0 = private unnamed_addr constant [4 x i8] c"cat\00", align 1
@sl.str1 = private unnamed_addr constant [4 x i8] c"dog\00", align 1
@sl.str2 = private unnamed_addr constant [4 x i8] c"moo\00", align 1
@sl.str3 = private unnamed_addr constant [4 x i8] c"boo\00", align 1
@sl.str4 = private unnamed_addr constant [4 x i8] c"hoo\00", align 1