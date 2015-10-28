package wich.codegen.model;

import wich.codegen.source.CodeGenerator;
import wich.semantics.SymbolTable;
import wich.semantics.symbols.WVariableSymbol;

public class StringVarDefStat extends VarDefStat {
	public StringVarDefStat(WVariableSymbol symbol) {
		super(symbol, CodeGenerator.getTypeModel(SymbolTable._string));
	}
}
