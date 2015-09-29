package wich.codegen.model.expr;

public class VarRef extends Expr {
	public String name;

	public VarRef(String name) {
		this.name = name;
	}
}
