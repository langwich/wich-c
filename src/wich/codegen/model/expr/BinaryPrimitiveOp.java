package wich.codegen.model.expr;

import wich.codegen.model.WichType;

public class BinaryPrimitiveOp extends BinaryOpExpr {
	public BinaryPrimitiveOp(Expr left, String op, Expr right, WichType type, String tempVar) {
		super(left, op, right, type, tempVar);
	}
}
