package wich.codegen.model;

import wich.codegen.model.expr.VarRef;

public class RefCountROOT extends Stat {
	@ModelElement public final VarRef varRef;

	public RefCountROOT(VarRef varRef) {
		this.varRef = varRef;
	}
}
