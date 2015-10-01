package wich.codegen.model.expr;

import org.antlr.symtab.Type;
import wich.semantics.symbols.WVariableSymbol;

public class VarRef extends Expr {
	public WVariableSymbol symbol;

	public VarRef(WVariableSymbol symbol) {
		this.symbol = symbol;
	}

	public String getName() { return symbol.getName(); }

	@Override
	public Type getType() {
		return symbol.getType();
	}
}
