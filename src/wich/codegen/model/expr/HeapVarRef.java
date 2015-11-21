package wich.codegen.model.expr;

import wich.codegen.model.WichType;
import wich.semantics.symbols.WVariableSymbol;

public class HeapVarRef extends VarRef {
	public HeapVarRef(WVariableSymbol symbol, WichType type) {
		super(symbol, type);
	}
}
