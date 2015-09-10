package wich.semantics.type;

import org.antlr.symtab.ArrayType;
import wich.semantics.SymbolTable;

public class WVector extends ArrayType {
    public WVector() {
        super(SymbolTable._float);
    }
}
