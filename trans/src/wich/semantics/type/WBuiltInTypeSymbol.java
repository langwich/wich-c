package wich.semantics.type;

import org.antlr.symtab.BaseSymbol;
import org.antlr.symtab.Type;

public class WBuiltInTypeSymbol extends BaseSymbol implements Type {
	public WBuiltInTypeSymbol(String name) {
		super(name);
	}

	@Override
	public String toString() {
		return name;
	}
}
