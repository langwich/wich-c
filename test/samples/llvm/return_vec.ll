%struct.heap_object = type {}
target triple = "x86_64-apple-macosx10.11.0"

; ///////// ///////// D A T A  S T R U C T U R E S ///////// /////////
%struct._object_metadata = type { i8*, i16, [0 x i16] }
%struct.Heap_Info = type { i8*, i8*, i8*, i32, i32, i32, i32, i32, i32, i32 }
%struct.__sFILE = type { i8*, i32, i32, i16, i16, %struct.__sbuf, i32, i8*, i32 (i8*)*, i32 (i8*, i8*, i32)*, i64 (i8*, i64, i32)*, i32 (i8*, i8*, i32)*, %struct.__sbuf, %struct.__sFILEX*, i32, [3 x i8], [1 x i8], %struct.__sbuf, i32, i64 }
%struct.__sFILEX = type opaque
%struct.__sbuf = type { i8*, i32 }
%struct.PVector_ptr = type { i32, %struct.PVector* }
%struct.PVector = type { %struct.heap_object, i32, i64, [0 x %struct._PVectorFatNode] }
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

declare %struct.string* @String_from_float(double)

declare void @print_string(%struct.string*)

declare zeroext i1 @String_eq(%struct.string*, %struct.string*)

declare zeroext i1 @String_neq(%struct.string*, %struct.string*)

declare zeroext i1 @String_gt(%struct.string*, %struct.string*)

declare zeroext i1 @String_ge(%struct.string*, %struct.string*)

declare zeroext i1 @String_lt(%struct.string*, %struct.string*)

declare zeroext i1 @String_le(%struct.string*, %struct.string*)

declare void (i32)* @signal(i32, void (i32)*)

; ///////// ///////// G C ///////// /////////

declare i32 @gc_num_roots(...)

declare void @gc_set_num_roots(i32)

declare void @gc_add_root(i8**)

declare void @gc(...)

declare void @get_heap_info(%struct.Heap_Info* sret, ...)

declare void @gc_shutdown(...)

; ///////// ///////// S Y S T E M  F U N C T I O N S ///////// /////////
declare i32 @fprintf(%struct.__sFILE*, i8*, ...)

declare void @exit(i32)

declare i32 @printf(i8*, ...)

declare void @llvm.memcpy.p0i8.p0i8.i64(i8* nocapture, i8* nocapture readonly, i64, i32, i1)

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
@NIL_VECTOR = internal constant %struct.PVector_ptr { i32 -1, %struct.PVector* null }, align 8
@pf.str = private unnamed_addr constant [7 x i8] c"%1.2f\0A\00", align 1
@pi.str = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1
@.str.2 = private unnamed_addr constant [8 x i8] c"UNKNOWN\00", align 1
@.str.3 = private unnamed_addr constant [8 x i8] c"SIGSEGV\00", align 1
@.str.4 = private unnamed_addr constant [7 x i8] c"SIGBUS\00", align 1
@__stderrp = external global %struct.__sFILE*, align 8
@.str.5 = private unnamed_addr constant [34 x i8] c"Wich is confused; signal %s (%d)\0A\00", align 1
@.str.6 = private unnamed_addr constant [36 x i8] c"%d objects remain after collection\0A\00", align 1
; ///////// ///////// G E N E R A T E D  C O D E ///////// /////////

define %struct.PVector_ptr @foo() {
entry:
%retval_ = alloca %struct.PVector_ptr
%0 = alloca [5 x double]
%promo0_ = getelementptr [5 x double], [5 x double]* %0, i64 0, i64 0
%1 = add i32 1, 0
%promo0 = sitofp i32 %1 to double
store double %promo0, double* %promo0_
%promo1_ = getelementptr [5 x double], [5 x double]* %0, i64 0, i64 1
%2 = add i32 2, 0
%promo1 = sitofp i32 %2 to double
store double %promo1, double* %promo1_
%promo2_ = getelementptr [5 x double], [5 x double]* %0, i64 0, i64 2
%3 = add i32 3, 0
%promo2 = sitofp i32 %3 to double
store double %promo2, double* %promo2_
%promo3_ = getelementptr [5 x double], [5 x double]* %0, i64 0, i64 3
%4 = add i32 4, 0
%promo3 = sitofp i32 %4 to double
store double %promo3, double* %promo3_
%promo4_ = getelementptr [5 x double], [5 x double]* %0, i64 0, i64 4
%5 = add i32 5, 0
%promo4 = sitofp i32 %5 to double
store double %promo4, double* %promo4_
%vec_ptr_6 = getelementptr [5 x double], [5 x double]* %0, i64 0, i64 0
%6 = call %struct.PVector_ptr @PVector_new(double* %vec_ptr_6, i64 5)
store %struct.PVector_ptr %6, %struct.PVector_ptr* %retval_
br label %ret_
return.exit_0:
br label %ret__
ret__:
br label %ret_

ret_:
%retval = load %struct.PVector_ptr, %struct.PVector_ptr* %retval_
ret %struct.PVector_ptr %retval
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
%x0_ = alloca %struct.PVector_ptr
%0 = call %struct.PVector_ptr () @foo()
store %struct.PVector_ptr %0, %struct.PVector_ptr* %x0_

%1 = call %struct.PVector_ptr () @foo()
call void (%struct.PVector_ptr) @print_vector(%struct.PVector_ptr %1)
br label %ret__
ret__:
br label %ret_

ret_:
%retval = load i32, i32* %retval_
ret i32 %retval
}


