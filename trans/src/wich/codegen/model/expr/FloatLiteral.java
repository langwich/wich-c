package wich.codegen.model.expr;

import org.antlr.symtab.Type;
import wich.semantics.SymbolTable;

public class FloatLiteral extends Expr {
	public String value;

	public FloatLiteral(String value) {
		this.value = value;
	}

	@Override
	public Type getType() {
		return SymbolTable._float;
	}
}
