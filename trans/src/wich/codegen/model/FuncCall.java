package wich.codegen.model;


import java.util.ArrayList;
import java.util.List;

public class FuncCall extends Expr{
	public final String funcName;
	@ModelElement public WichType retType;
	public Integer localTmp = null;
	@ModelElement public List<Expr> args = new ArrayList<>();

	public FuncCall(String funcName) {
		this.funcName = funcName;
	}
}
