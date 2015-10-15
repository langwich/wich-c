package wich.codegen.model;

import wich.semantics.symbols.WVariableSymbol;

/** Represents a variable definition */
public class VarDefStat extends Stat {
	public final WVariableSymbol symbol;
	@ModelElement public WichType type;

	public VarDefStat(WVariableSymbol symbol, WichType type) {
		this.symbol = symbol;
		this.type = type;
	}

	public String getName() { return symbol.getName(); }
}
