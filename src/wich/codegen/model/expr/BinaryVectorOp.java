package wich.codegen.model.expr;

import wich.codegen.model.VectorType;

public class BinaryVectorOp extends BinaryOpExpr {
	public BinaryVectorOp(Expr left, String op, Expr right, String tempVarRef) {
		super(left, op, right, new VectorType(), tempVarRef);
	}
}
