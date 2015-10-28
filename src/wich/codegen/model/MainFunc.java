package wich.codegen.model;

import wich.codegen.source.CodeGenerator;
import wich.semantics.SymbolTable;
import wich.semantics.symbols.WFunctionSymbol;

public class MainFunc extends Func {
	public MainFunc(WFunctionSymbol scope, FuncBlock body) {
		super(scope, CodeGenerator.getTypeModel(SymbolTable._int), body);
	}
}