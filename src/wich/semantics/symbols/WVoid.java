package wich.semantics.symbols;

import wich.codegen.CompilerUtils;

public class WVoid extends WBuiltInTypeSymbol {
	public WVoid() {
		super("void", TYPENAME.VOID);
	}

	public int getVMTypeIndex() {
		return CompilerUtils.VOID_TYPE;
	}
}
