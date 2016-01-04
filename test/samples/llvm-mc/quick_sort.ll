%struct.heap_object = type { i32, %struct._object_metadata*, i32, i8, %struct.heap_object* }
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

declare i32 @Vector_len(%struct.PVector_ptr)

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

declare i32 @String_len(%struct.string*)

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

define %struct.PVector_ptr @quickSort(%struct.PVector_ptr %nums0, i32 %lo0, i32 %hi0) {
entry:
%nums0_ = alloca %struct.PVector_ptr
store %struct.PVector_ptr %nums0, %struct.PVector_ptr* %nums0_
%lo0_ = alloca i32
store i32 %lo0, i32* %lo0_
%hi0_ = alloca i32
store i32 %hi0, i32* %hi0_
%retval_ = alloca %struct.PVector_ptr
%_funcsp = alloca i32, align 4
%____num_roots = call i32 (...) @gc_num_roots()
store i32 %____num_roots, i32* %_funcsp, align 4
%p0_ = alloca i32
%x0_ = alloca double
%j0_ = alloca i32
%i0_ = alloca i32
%temp1_ = alloca double
%l0_ = alloca %struct.PVector_ptr
%l0_mcp_target_ = bitcast %struct.PVector_ptr* %l0_ to i8*
call void @llvm.memcpy.p0i8.p0i8.i64(i8* %l0_mcp_target_, i8* bitcast (%struct.PVector_ptr* @NIL_VECTOR to i8*), i64 16, i32 8, i1 false)
%l0_inner_ptr_ = getelementptr inbounds %struct.PVector_ptr, %struct.PVector_ptr* %l0_, i32 0, i32 1
%l0_raw_ptr_ = bitcast %struct.PVector** %l0_inner_ptr_ to i8**
call void @gc_add_root(i8** %l0_raw_ptr_)
%r0_ = alloca %struct.PVector_ptr
%r0_mcp_target_ = bitcast %struct.PVector_ptr* %r0_ to i8*
call void @llvm.memcpy.p0i8.p0i8.i64(i8* %r0_mcp_target_, i8* bitcast (%struct.PVector_ptr* @NIL_VECTOR to i8*), i64 16, i32 8, i1 false)
%r0_inner_ptr_ = getelementptr inbounds %struct.PVector_ptr, %struct.PVector_ptr* %r0_, i32 0, i32 1
%r0_raw_ptr_ = bitcast %struct.PVector** %r0_inner_ptr_ to i8**
call void @gc_add_root(i8** %r0_raw_ptr_)
%0 = load i32, i32* %lo0_
%1 = load i32, i32* %hi0_
%2 = icmp sge i32 %0, %1
br i1 %2, label %if.block_true_0, label %if.block_false_0
if.block_true_0:
%3 = load %struct.PVector_ptr, %struct.PVector_ptr* %nums0_
store %struct.PVector_ptr %3, %struct.PVector_ptr* %retval_
br label %ret_
return.exit_0:

br label %if.block_exit_0
if.block_false_0:
br label %if.block_exit_0
if.block_exit_0:
%4 = add i32 0, 0
store i32 %4, i32* %p0_
%vec_6 = load %struct.PVector_ptr, %struct.PVector_ptr* %nums0_
%5 = load i32, i32* %hi0_
%index_5 = sub i32 %5, 1
%6 = call double (%struct.PVector_ptr, i32) @ith(%struct.PVector_ptr %vec_6, i32 %index_5)
store double %6, double* %x0_
%7 = load i32, i32* %lo0_
store i32 %7, i32* %j0_
%8 = load i32, i32* %lo0_
%9 = add i32 1, 0
%10 = sub i32 %8, %9
store i32 %10, i32* %i0_
br label %while.block_entry_0
while.block_entry_0:
%11 = load i32, i32* %j0_
%12 = load i32, i32* %hi0_
%13 = icmp slt i32 %11, %12
br i1 %13, label %while.block_body_0, label %while.block_exit_0
while.block_body_0:
%vec_15 = load %struct.PVector_ptr, %struct.PVector_ptr* %nums0_
%14 = load i32, i32* %j0_
%index_14 = sub i32 %14, 1
%15 = call double (%struct.PVector_ptr, i32) @ith(%struct.PVector_ptr %vec_15, i32 %index_14)
%16 = load double, double* %x0_
%17 = fcmp ole double %15, %16
br i1 %17, label %if.block_true_1, label %if.block_false_1
if.block_true_1:
%temp0_ = alloca double
%18 = load i32, i32* %i0_
%19 = add i32 1, 0
%20 = add i32 %18, %19
store i32 %20, i32* %i0_
%vec_22 = load %struct.PVector_ptr, %struct.PVector_ptr* %nums0_
%21 = load i32, i32* %j0_
%index_21 = sub i32 %21, 1
%22 = call double (%struct.PVector_ptr, i32) @ith(%struct.PVector_ptr %vec_22, i32 %index_21)
store double %22, double* %temp0_
%23 = load %struct.PVector_ptr, %struct.PVector_ptr* %nums0_
%24 = load i32, i32* %j0_
%index_24 = sub i32 %24, 1
%vec_26 = load %struct.PVector_ptr, %struct.PVector_ptr* %nums0_
%25 = load i32, i32* %i0_
%index_25 = sub i32 %25, 1
%26 = call double (%struct.PVector_ptr, i32) @ith(%struct.PVector_ptr %vec_26, i32 %index_25)
call void (%struct.PVector_ptr,i32,double) @set_ith(%struct.PVector_ptr %23, i32 %index_24, double %26)
%27 = load %struct.PVector_ptr, %struct.PVector_ptr* %nums0_
%28 = load i32, i32* %i0_
%index_28 = sub i32 %28, 1
%29 = load double, double* %temp0_
call void (%struct.PVector_ptr,i32,double) @set_ith(%struct.PVector_ptr %27, i32 %index_28, double %29)

br label %if.block_exit_1
if.block_false_1:
br label %if.block_exit_1
if.block_exit_1:
%30 = load i32, i32* %j0_
%31 = add i32 1, 0
%32 = add i32 %30, %31
store i32 %32, i32* %j0_

br label %while.block_entry_0
while.block_exit_0:
%33 = load i32, i32* %i0_
%34 = add i32 1, 0
%35 = add i32 %33, %34
store i32 %35, i32* %p0_
%vec_37 = load %struct.PVector_ptr, %struct.PVector_ptr* %nums0_
%36 = load i32, i32* %p0_
%index_36 = sub i32 %36, 1
%37 = call double (%struct.PVector_ptr, i32) @ith(%struct.PVector_ptr %vec_37, i32 %index_36)
store double %37, double* %temp1_
%38 = load %struct.PVector_ptr, %struct.PVector_ptr* %nums0_
%39 = load i32, i32* %p0_
%index_39 = sub i32 %39, 1
%vec_41 = load %struct.PVector_ptr, %struct.PVector_ptr* %nums0_
%40 = load i32, i32* %hi0_
%index_40 = sub i32 %40, 1
%41 = call double (%struct.PVector_ptr, i32) @ith(%struct.PVector_ptr %vec_41, i32 %index_40)
call void (%struct.PVector_ptr,i32,double) @set_ith(%struct.PVector_ptr %38, i32 %index_39, double %41)
%42 = load %struct.PVector_ptr, %struct.PVector_ptr* %nums0_
%43 = load i32, i32* %hi0_
%index_43 = sub i32 %43, 1
%44 = load double, double* %temp1_
call void (%struct.PVector_ptr,i32,double) @set_ith(%struct.PVector_ptr %42, i32 %index_43, double %44)
%45 = load %struct.PVector_ptr, %struct.PVector_ptr* %nums0_
%46 = call %struct.PVector_ptr @Vector_copy(%struct.PVector_ptr %45)
%47 = load i32, i32* %lo0_
%48 = load i32, i32* %p0_
%49 = add i32 1, 0
%50 = sub i32 %48, %49
%51 = call %struct.PVector_ptr (%struct.PVector_ptr,i32,i32) @quickSort(%struct.PVector_ptr %46,i32 %47,i32 %50)
%52 = call %struct.PVector_ptr @Vector_copy(%struct.PVector_ptr %51)
store %struct.PVector_ptr %52, %struct.PVector_ptr* %l0_
%53 = load %struct.PVector_ptr, %struct.PVector_ptr* %l0_
%54 = call %struct.PVector_ptr @Vector_copy(%struct.PVector_ptr %53)
%55 = load i32, i32* %p0_
%56 = add i32 1, 0
%57 = add i32 %55, %56
%58 = load i32, i32* %hi0_
%59 = call %struct.PVector_ptr (%struct.PVector_ptr,i32,i32) @quickSort(%struct.PVector_ptr %54,i32 %57,i32 %58)
%60 = call %struct.PVector_ptr @Vector_copy(%struct.PVector_ptr %59)
store %struct.PVector_ptr %60, %struct.PVector_ptr* %r0_
%61 = load %struct.PVector_ptr, %struct.PVector_ptr* %r0_
store %struct.PVector_ptr %61, %struct.PVector_ptr* %retval_
br label %ret_
return.exit_1:
br label %ret__
ret__:
br label %ret_

ret_:
%num_roots____ = load i32, i32* %_funcsp, align 4
call void @gc_set_num_roots(i32 %num_roots____)
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
%__info__ = alloca %struct.Heap_Info, align 8
%_funcsp = alloca i32, align 4
%____num_roots = call i32 (...) @gc_num_roots()
store i32 %____num_roots, i32* %_funcsp, align 4
%v0_ = alloca %struct.PVector_ptr
%v0_mcp_target_ = bitcast %struct.PVector_ptr* %v0_ to i8*
call void @llvm.memcpy.p0i8.p0i8.i64(i8* %v0_mcp_target_, i8* bitcast (%struct.PVector_ptr* @NIL_VECTOR to i8*), i64 16, i32 8, i1 false)
%v0_inner_ptr_ = getelementptr inbounds %struct.PVector_ptr, %struct.PVector_ptr* %v0_, i32 0, i32 1
%v0_raw_ptr_ = bitcast %struct.PVector** %v0_inner_ptr_ to i8**
call void @gc_add_root(i8** %v0_raw_ptr_)
%0 = alloca [9 x double]
%vpromo0_ = getelementptr [9 x double], [9 x double]* %0, i64 0, i64 0
%1 = add i32 1, 0
%promo0 = sitofp i32 %1 to double
store double %promo0, double* %vpromo0_
%vpromo1_ = getelementptr [9 x double], [9 x double]* %0, i64 0, i64 1
%2 = add i32 3, 0
%promo1 = sitofp i32 %2 to double
store double %promo1, double* %vpromo1_
%vpromo2_ = getelementptr [9 x double], [9 x double]* %0, i64 0, i64 2
%3 = add i32 4, 0
%promo2 = sitofp i32 %3 to double
store double %promo2, double* %vpromo2_
%vpromo3_ = getelementptr [9 x double], [9 x double]* %0, i64 0, i64 3
%4 = add i32 5, 0
%promo3 = sitofp i32 %4 to double
store double %promo3, double* %vpromo3_
%vpromo4_ = getelementptr [9 x double], [9 x double]* %0, i64 0, i64 4
%5 = add i32 2, 0
%promo4 = sitofp i32 %5 to double
store double %promo4, double* %vpromo4_
%vpromo5_ = getelementptr [9 x double], [9 x double]* %0, i64 0, i64 5
%6 = add i32 100, 0
%promo5 = sitofp i32 %6 to double
store double %promo5, double* %vpromo5_
%vpromo6_ = getelementptr [9 x double], [9 x double]* %0, i64 0, i64 6
%7 = add i32 99, 0
%promo6 = sitofp i32 %7 to double
store double %promo6, double* %vpromo6_
%vpromo7_ = getelementptr [9 x double], [9 x double]* %0, i64 0, i64 7
%8 = add i32 0, 0
%promo7 = sitofp i32 %8 to double
store double %promo7, double* %vpromo7_
%v10_ = getelementptr [9 x double], [9 x double]* %0, i64 0, i64 8
%9 = add i32 1, 0
%promo8 = sitofp i32 %9 to double
%10 = fsub double 0.0, %promo8
store double %10, double* %v10_
%vec_ptr_11 = getelementptr [9 x double], [9 x double]* %0, i64 0, i64 0
%11 = call %struct.PVector_ptr @PVector_new(double* %vec_ptr_11, i64 9)
store %struct.PVector_ptr %11, %struct.PVector_ptr* %v0_
%12 = load %struct.PVector_ptr, %struct.PVector_ptr* %v0_
%13 = call %struct.PVector_ptr @Vector_copy(%struct.PVector_ptr %12)
%14 = add i32 1, 0
%15 = add i32 9, 0
%16 = call %struct.PVector_ptr (%struct.PVector_ptr,i32,i32) @quickSort(%struct.PVector_ptr %13,i32 %14,i32 %15)
call void (%struct.PVector_ptr) @print_vector(%struct.PVector_ptr %16)
br label %ret__
ret__:
br label %ret_

ret_:
%num_roots____ = load i32, i32* %_funcsp, align 4
call void @gc_set_num_roots(i32 %num_roots____)
call void (...) @gc()
call void (%struct.Heap_Info*, ...) @get_heap_info(%struct.Heap_Info* sret %__info__)
%info___ = getelementptr inbounds  %struct.Heap_Info, %struct.Heap_Info* %__info__, i32 0, i32 5
%info__ = load i32, i32* %info___, align 4
%heap_dirty__ = icmp ne i32 %info__, 0
br i1 %heap_dirty__, label %gc.heap_dirty____, label %gc.heap_clean____
gc.heap_dirty____:
%__stderr = load %struct.__sFILE*, %struct.__sFILE** @__stderrp, align 8
%remain_num___ = getelementptr inbounds  %struct.Heap_Info, %struct.Heap_Info* %__info__, i32 0, i32 5
%remain_num__ = load i32, i32* %remain_num___, align 4
%__fprintfret = call i32 (%struct.__sFILE*, i8*, ...) @fprintf(%struct.__sFILE* %__stderr, i8* getelementptr inbounds ([36 x i8], [36 x i8]* @.str.6, i32 0, i32 0), i32 %remain_num__)
br label %gc.heap_clean____
gc.heap_clean____:
call void (...) @gc_shutdown()
%retval = load i32, i32* %retval_
ret i32 %retval
}


