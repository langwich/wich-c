package wich.codegen.model;

import wich.codegen.model.expr.VarRef;

public class RefCountREF extends Stat {
	@ModelElement public final VarRef varRef;

	public RefCountREF(VarRef varRef) {
		this.varRef = varRef;
	}
}
