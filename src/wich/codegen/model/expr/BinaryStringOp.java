package wich.codegen.model.expr;

import wich.codegen.model.StringType;
import wich.codegen.model.WichType;

public class BinaryStringOp extends BinaryOpExpr {
	public BinaryStringOp(Expr left, String op, Expr right) {
		super(left, op, right, new StringType());
	}
}
