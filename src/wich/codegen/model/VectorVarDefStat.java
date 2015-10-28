package wich.codegen.model;

import wich.codegen.source.CodeGenerator;
import wich.semantics.SymbolTable;
import wich.semantics.symbols.WVariableSymbol;

public class VectorVarDefStat extends VarDefStat {
	public VectorVarDefStat(WVariableSymbol symbol) {
		super(symbol, CodeGenerator.getTypeModel(SymbolTable._vector));
	}
}
