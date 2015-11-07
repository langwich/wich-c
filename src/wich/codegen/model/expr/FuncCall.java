package wich.codegen.model.expr;


import org.antlr.symtab.Type;
import wich.codegen.model.ModelElement;
import wich.codegen.model.WichType;

import java.util.ArrayList;
import java.util.List;

public class FuncCall extends Expr {
	public final String funcName;
	@ModelElement public List<Expr> args = new ArrayList<>();

	public FuncCall(String funcName, WichType type) {
		this.funcName = funcName;
		this.type = type;
	}

	@Override
	public Type getType() {
		return type.type;
	}
}
