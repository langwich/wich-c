package wich.semantics.type;

import org.antlr.symtab.BaseSymbol;
import org.antlr.symtab.Type;

public class WBuiltInTypeSymbol extends BaseSymbol implements Type {
	protected int typeIndex;
	protected WBuiltInTypeSymbol promotedType;

	public WBuiltInTypeSymbol(String name) {
		super(name);
	}

	@Override
	public String toString() {
		return name;
	}

	public int getTypeIndex() {
		return typeIndex;
	}

	public WBuiltInTypeSymbol getPromotedType() {
		return promotedType;
	}

	public void setPromotedType(WBuiltInTypeSymbol promotedType) {
		this.promotedType = promotedType;
	}
}
