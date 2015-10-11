package wich.codegen.model.expr;

import org.antlr.symtab.Type;
import wich.semantics.SymbolTable;

public class FalseLiteral extends Expr{
	public String value;

	public FalseLiteral(String value) {
		this.value = value;
	}

	@Override
	public Type getType() {
		return SymbolTable._boolean;
	}
}
