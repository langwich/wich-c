package wich.codegen.model.expr;

import wich.codegen.model.VectorType;

public class BinaryVectorOp extends BinaryOpExpr {
	public BinaryVectorOp(Expr left, String op, Expr right) {
		super(left, op, right, new VectorType());
	}
}
