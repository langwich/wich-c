package wich.codegen.model;

import wich.codegen.model.expr.VarRef;

public class RefCountDEREF extends Stat {
	@ModelElement public final VarRef varRef;

	public RefCountDEREF(VarRef varRef) {
		this.varRef = varRef;
	}
}
