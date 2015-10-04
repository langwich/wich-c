package wich.codegen.model;

import wich.codegen.CodeGenerator;
import wich.semantics.SymbolTable;
import wich.semantics.symbols.WVariableSymbol;

public class VectorVarDefStat extends VarDefStat {
	public VectorVarDefStat(WVariableSymbol symbol) {
		super(symbol, CodeGenerator.getTypeModel(SymbolTable._vector));
	}
}
