package wich.semantics.type;

import org.antlr.symtab.Type;
import wich.semantics.SymbolTable;

public class WVector extends WBuiltInTypeSymbol {
	protected final Type elemType = SymbolTable._float;
    public WVector() {
        super("vector");
    }
}
