package wich.codegen.model;

import wich.codegen.CodeGenerator;
import wich.semantics.SymbolTable;
import wich.semantics.symbols.WVariableSymbol;

public class StringVarDefStat extends VarDefStat {
	public StringVarDefStat(WVariableSymbol symbol) {
		super(symbol, CodeGenerator.getTypeModel(SymbolTable._string));
	}
}
