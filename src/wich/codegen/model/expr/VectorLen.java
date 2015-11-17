package wich.codegen.model.expr;


import org.antlr.symtab.Type;
import wich.codegen.model.ModelElement;
import wich.codegen.model.expr.Expr;
import wich.semantics.SymbolTable;

public class VectorLen extends Expr {
	@ModelElement public Expr expr;

	public VectorLen(Expr expr) {
		this.expr = expr;
	}

	@Override
	public Type getType() {
		return SymbolTable._int;
	}
}
