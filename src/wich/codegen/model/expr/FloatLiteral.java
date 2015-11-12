package wich.codegen.model.expr;

import org.antlr.symtab.Type;
import wich.codegen.model.FloatType;
import wich.semantics.SymbolTable;

public class FloatLiteral extends Expr {
	public String value;

	public FloatLiteral(String value, String tempVar) {
		this.value = value;
		this.type = new FloatType();
		this.varRef = tempVar;
	}

	@Override
	public Type getType() {
		return SymbolTable._float;
	}
}
