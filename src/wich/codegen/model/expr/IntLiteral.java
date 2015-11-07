package wich.codegen.model.expr;

import org.antlr.symtab.Type;
import wich.codegen.model.IntType;
import wich.semantics.SymbolTable;

public class IntLiteral extends Expr {
	public String value;

	public IntLiteral(String value) {
		this.value = value;
		this.type = new IntType();
	}

	@Override
	public Type getType() {
		return SymbolTable._int;
	}
}
