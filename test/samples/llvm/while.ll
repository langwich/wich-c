target triple = "x86_64-apple-macosx10.11.0"
%PVector = type { %heap_object, i32, i64, [0 x %_PVectorFatNode] }
%heap_object = type {}
%_PVectorFatNode = type { double, %_PVectorFatNodeElem* }
%_PVectorFatNodeElem = type { %heap_object, i32, double, %_PVectorFatNodeElem* }
%PVector_ptr = type { i32, %PVector* }

; Function Attrs: nounwind
define i32 @main(i32 %argc, i8** %argv) {
entry:
	br label %while.body

while.body:                                       ; preds = %entry, %while.body
	%x = phi i32 [ 100, %entry ], [ %sub, %while.body ]

	%conv_x = sitofp i32 %x to double
	%add = fadd double %conv_x, 1.000000e+00
	%call = tail call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([7 x i8], [7 x i8]* @.str, i64 0, i64 0), double %add)
	%sub = add i32 %x, -1
	%cmp = icmp sgt i32 %x, 1
	br i1 %cmp, label %while.body, label %while.end

while.end:                                        ; preds = %while.body
	ret i32 0
}

declare i32 @printf(i8*, ...)

@.str = private unnamed_addr constant [7 x i8] c"%1.2f\0A\00", align 1