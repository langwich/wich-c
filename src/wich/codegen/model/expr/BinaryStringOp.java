package wich.codegen.model.expr;

import wich.codegen.model.ModelElement;
import wich.codegen.model.StringType;
import wich.codegen.model.WichType;

public class BinaryStringOp extends BinaryOpExpr {
	@ModelElement public WichType resType;
	public BinaryStringOp(Expr left, String op, Expr right, WichType type, WichType resType, String tempVar) {
		super(left, op, right, type, tempVar);
		this.resType = resType;
	}
}
