package wich.codegen.model.expr;

import wich.semantics.symbols.WVariableSymbol;

public class HeapVarRef extends VarRef {
	public HeapVarRef(WVariableSymbol symbol, boolean isAssign) {
		super(symbol,null, isAssign);
	}
}
