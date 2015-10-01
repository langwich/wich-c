package wich.codegen.model.expr;

import wich.semantics.symbols.WVariableSymbol;

public class HeapVarRef extends VarRef {
	/** Local variable index into _localptrs array */
	public int index;

	public HeapVarRef(WVariableSymbol symbol) {
		super(symbol);
	}
}
