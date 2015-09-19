package wich.codegen.model;


import java.util.ArrayList;
import java.util.List;

public class FuncCall extends Expr{
	public final String funcName;
	public String reType;
	@ModelElement public List<Expr> args = new ArrayList<>();
	@ModelElement public TmpVarDef localTmp ;

	public FuncCall(String funcName) {
		this.funcName = funcName;
	}
}
