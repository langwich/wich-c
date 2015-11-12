package wich.codegen.model.expr;

import org.antlr.symtab.Type;
import wich.codegen.model.BooleanType;
import wich.semantics.SymbolTable;

public class FalseLiteral extends Expr {
	public String value;

	public FalseLiteral(String value, String tempVar) {
		this.value = value;
		this.type = new BooleanType();
		this.varRef = tempVar;
	}

	@Override
	public Type getType() {
		return SymbolTable._boolean;
	}
}
