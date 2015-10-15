package wich.codegen.model;

import wich.semantics.SymbolTable;

public class VoidType extends PrimitiveType {
	public VoidType() {
		super(SymbolTable._void);
	}
}
