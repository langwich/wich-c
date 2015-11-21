package wich.codegen.model.expr;

import wich.codegen.model.ModelElement;
import wich.codegen.model.WichType;

public class BinaryVectorOp extends BinaryOpExpr {
	@ModelElement public WichType resType;

	public BinaryVectorOp(Expr left, String op, Expr right, WichType type, WichType resType, String tempVarRef) {
		super(left, op, right, type, tempVarRef);
		this.resType = resType;
	}
}
