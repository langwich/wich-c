package wich.codegen.model.expr;


import org.antlr.symtab.Type;
import wich.codegen.model.ModelElement;
import wich.codegen.model.WichType;

import java.util.ArrayList;
import java.util.List;

public class FuncCall extends Expr {
	public final String funcName;
	@ModelElement public WichType retType;
	@ModelElement public List<Expr> args = new ArrayList<>();

	public FuncCall(String funcName, WichType retType) {
		this.funcName = funcName;
		this.retType = retType;
	}

	@Override
	public Type getType() {
		return retType.type;
	}
}
