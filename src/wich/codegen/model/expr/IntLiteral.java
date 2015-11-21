package wich.codegen.model.expr;

import org.antlr.symtab.Type;
import wich.codegen.model.IntType;
import wich.semantics.SymbolTable;

public class IntLiteral extends Expr {
	public String value;

	public IntLiteral(String value, String tempVar) {
		this.value = value;
		this.type = new IntType();
		this.varRef = tempVar;
	}

	@Override
	public Type getType() {
		return SymbolTable._int;
	}
}
