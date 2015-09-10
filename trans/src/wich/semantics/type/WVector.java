package wich.semantics.type;

import org.antlr.symtab.Type;
import wich.semantics.SymbolTable;

public class WVector extends WBuiltInTypeSymbol implements Type {
	protected final Type elemType = SymbolTable._float;
    public WVector() {
        super("vector");
    }
}
