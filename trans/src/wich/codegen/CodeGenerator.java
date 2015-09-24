/*
The MIT License (MIT)

Copyright (c) 2015 Terence Parr, Hanzhou Shi, Shuai Yuan, Yuanyuan Zhang

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package wich.codegen;

import org.antlr.symtab.Scope;
import org.antlr.symtab.Symbol;
import org.antlr.symtab.Type;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import wich.codegen.model.*;
import wich.parser.WichBaseVisitor;
import wich.parser.WichParser;
import wich.semantics.SymbolTable;
import wich.semantics.symbols.*;

import java.util.List;

public class CodeGenerator extends WichBaseVisitor<OutputModelObject> {
	public STGroup templates;
	public String fileName;
	protected final SymbolTable symtab;
	public Scope currentScope;
	private static int tmpIndex = 1;

	private int getTmpIndex(){
		return tmpIndex++;
	}

	public CodeGenerator(String fileName,SymbolTable symtab) {
		this.templates = new STGroupFile("wich.stg");
		this.symtab = symtab;
		this.fileName = fileName;
	}

	public CFile generate(ParserRuleContext tree) {
		CFile cFile = (CFile)visit(tree);
		return cFile;
	}

	@Override
	public OutputModelObject visitFile(@NotNull WichParser.FileContext ctx) {
		CFile cFile = new CFile();
		cFile.script = (Script)visit(ctx.script());
		return cFile;
	}

	@Override
	public OutputModelObject visitScript(@NotNull WichParser.ScriptContext ctx) {
		pushScope(symtab.getGlobalScope());
		Script script = new Script(fileName);
		List<WichParser.StatementContext> stats = ctx.statement();
		for (WichParser.StatementContext s:stats) {
			Stat stat = (Stat)visit(s);
			for(Integer i: stat.tmpVars){
				script.localTemps.add(i);
			}
			if (s instanceof WichParser.VarDefContext) {
				script.varDefs.add((VarDefStat) stat);
				Type t =((WVariableSymbol) currentScope.resolve(((WichParser.VarDefContext) s).ID().getText())).getType();
				if( isHeapObject(t)) {
					script.localVars.add(((WichParser.VarDefContext) s).ID().getText());
				}
			}
			else {
				script.stats.add(stat);
			}
		}
		List<WichParser.FunctionContext> funcs = ctx.function();
		for (WichParser.FunctionContext f:funcs) {
			script.functions.add((Func)visit(f));
		}
		tmpIndex =1;
		return script;
	}

	@Override
	public OutputModelObject visitFunction(@NotNull WichParser.FunctionContext ctx) {
		pushScope(ctx.scope);
		Func func = new Func(ctx.ID().getText());
		func.body = (Block)visit(ctx.block());
		if (ctx.formal_args()!= null) {
			List<WichParser.Formal_argContext> args = ctx.formal_args().formal_arg();
			for (WichParser.Formal_argContext arg:args) {
				func.args.add((ArgDef)visit(arg));
			}
		}
		if (ctx.type() != null) {
			if (ctx.type().getText().equals(SymbolTable._vector.getName())) {
				func.returnType = new NonCType(typeNameConvert(SymbolTable._vector.getName()));
			}
			else if (ctx.type().getText().equals(SymbolTable._string.getName())) {
				func.returnType = new NonCType(typeNameConvert(SymbolTable._string.getName()));
			} else{
				func.returnType = new CType(ctx.type().getText());
			}
		}
		popScope();
		return func;
	}

	@Override
	public OutputModelObject visitBlock(@NotNull WichParser.BlockContext ctx) {
		pushScope(ctx.scope);
		Block block = new Block();
		List<WichParser.StatementContext> stats = ctx.statement();
		for (WichParser.StatementContext s:stats) {
			Stat stat = (Stat)visit(s);
			for(Integer i: stat.tmpVars){
				block.localTemps.add(i);
			}
			if (s instanceof WichParser.VarDefContext) {
				block.varDefs.add((VarDefStat) stat);
				Type t =((WVariableSymbol) currentScope.resolve(((WichParser.VarDefContext) s).ID().getText())).getType();
				if( isHeapObject(t)) {
					block.localVars.add(((WichParser.VarDefContext) s).ID().getText());
				}
			}
			else if (s instanceof WichParser.ReturnContext) {
				SetupReturnStat(block, (WichParser.ReturnContext) s, stat);
			}
			else {
				block.stats.add(stat);
			}
		}
		addFunArgsRef(ctx, block);
		popScope();
		return block;
	}

	private void SetupReturnStat(Block block, WichParser.ReturnContext s, Stat stat) {
		block.returnStat = stat;
		Type type = s.expr().exprType;
		String var = s.expr().getText();
		if (((ReturnStat)stat).tmpIndex != null){
			ReturnTmpExpr e =new ReturnTmpExpr();
			e.expr = ((ReturnStat)stat).rExpr;
			block.returnTmpAssign = e;
		}
		if (isHeapObject(type) && (!isTemporySymbol(var))) {
			block.returnRefVar = var;
		}
		else if (((ReturnStat)stat).localTemps != null) {
			block.returnTemps = ((ReturnStat) stat).localTemps;
			if (block.returnTemps.size() >= 1) {
				block.returnRefVar = "tmp" + (block.returnTemps.get(block.returnTemps.size() - 1)).index;
			}
		}
	}

	private boolean isHeapObject(Type type) {
		if (type instanceof WString || type instanceof WVector) {
			return true;
		}
		return false;
	}

	private void addFunArgsRef(@NotNull WichParser.BlockContext ctx, Block block) {
		if (ctx.getParent() instanceof WichParser.FunctionContext) {
			if (((WichParser.FunctionContext) ctx.getParent()).formal_args() == null) return;
			List<WichParser.Formal_argContext> args = ((WichParser.FunctionContext) ctx.getParent()).formal_args().formal_arg();
			for (WichParser.Formal_argContext a : args) {
				if (a.type().getText().equals("string")||a.type().getText().equals("[]")) {
					block.argsRef.add(a.ID().getText());
					block.localVars.add(a.ID().getText());
				}
			}
		}
	}

	@Override
	public OutputModelObject visitFormal_arg(@NotNull WichParser.Formal_argContext ctx) {
		ArgDef arg = new ArgDef(ctx.ID().getText());
		if (ctx.type().getText().equals(SymbolTable._vector.getName())) {
			arg.type = new NonCType(typeNameConvert(SymbolTable._vector.getName()));
		}
		else if (ctx.type().getText().equals(SymbolTable._string.getName())) {
			arg.type = new NonCType(typeNameConvert(SymbolTable._string.getName()));
		} else {
			arg.type = new CType(ctx.type().getText());
		}
		return arg;
	}

	@Override
	public OutputModelObject visitIf(@NotNull WichParser.IfContext ctx) {
		IfStat ifStat = new IfStat();
		ifStat.condition = (Expr)visit(ctx.expr());
		ifStat.stat = (Stat)visit(ctx.statement(0));
		if (ctx.statement().size()>1) {
			ifStat.elseStat = (Stat)visit(ctx.statement(1));
		}
		return ifStat;
	}

	@Override
	public OutputModelObject visitWhile(@NotNull WichParser.WhileContext ctx) {
		WhileStat whileStat = new WhileStat();
		whileStat.condition = (Expr)visit(ctx.expr());
		whileStat.stat = (Stat)visit(ctx.statement());
		return whileStat;
	}

	@Override
	public OutputModelObject visitVarDef(@NotNull WichParser.VarDefContext ctx) {
		VarDefStat varDef = new VarDefStat(ctx.ID().getText());
		WVariableSymbol v= ((WVariableSymbol)currentScope.resolve(ctx.ID().getText()));
		if (isHeapObject(v.getType())) {
			varDef.type = new NonCType(typeNameConvert(v.getType().getName()));
		}
		else {
			varDef.type = new CType(v.getType().getName());
		}
		varDef.expr = (Expr)visit(ctx.expr());
		varDef.localTemps = varDef.expr.tmpVarDefs;
		for (TmpVarDef t :varDef.localTemps) {
			varDef.tmpVars.add(t.index);
		}
		if (!isTemporySymbol(ctx.expr().getText())) {
			varDef.ref = varDef.name;
		}
		return varDef;
	}

	@Override
	public OutputModelObject visitAssign(@NotNull WichParser.AssignContext ctx) {
		AssignStat assignStat = new AssignStat(ctx.ID().getText());
		assignStat.right = (Expr)visit(ctx.expr());
		assignStat.localTemps =(assignStat.right).tmpVarDefs;
		for (TmpVarDef t :assignStat.localTemps) {
			assignStat.tmpVars.add(t.index);
		}
		return assignStat;
	}

	@Override
	public OutputModelObject visitElementAssign(@NotNull WichParser.ElementAssignContext ctx) {
		ElementAssignStat eAssignStat = new ElementAssignStat(ctx.ID().getText());
		eAssignStat.index = (Expr)visit(ctx.expr(0));
		eAssignStat.rExpr = (Expr)visit(ctx.expr(1));
		return eAssignStat;
	}

	@Override
	public OutputModelObject visitCallStatement(@NotNull WichParser.CallStatementContext ctx) {
		CallStat callStat = new CallStat();
		callStat.callExpr = (Expr)visit(ctx.call_expr());
		callStat.localTemps = (callStat.callExpr).tmpVarDefs;
		for (TmpVarDef t :callStat.localTemps) {
			callStat.tmpVars.add(t.index);
		}
		return callStat;
	}

	@Override
	public OutputModelObject visitCall_expr(@NotNull WichParser.Call_exprContext ctx) {
		String funcName =ctx.ID().getText();
		FuncCall fc = new FuncCall(funcName);
		Type type = null;
		if (((WFunctionSymbol) currentScope.resolve(funcName)).getType()!= null) {
			type = ((WFunctionSymbol) currentScope.resolve(funcName)).getType();
			fc.reType = type.getName();
		}
		if(ctx.expr_list() != null) {
			List<WichParser.ExprContext> exprs = ctx.expr_list().expr();
			for (WichParser.ExprContext e : exprs) {
				Expr expr = (Expr)visit(e);
				fc.args.add(expr);
				for (TmpVarDef t :expr.tmpVarDefs) {
					fc.tmpVarDefs.add(t);
				}
			}
		}
		if (isTempVarNeeded(ctx.getParent()) && type != null && isHeapObject(type)) {
			TmpVarDef t = new TmpVarDef(getTmpIndex(), typeNameConvert(fc.reType));
			fc.localTmp = t.index;
			fc.tmpVarDefs.add(t);
		}
		return fc;
	}

	@Override
	public OutputModelObject visitPrint(@NotNull WichParser.PrintContext ctx) {
		PrintStat printStat = new PrintStat();
		if (ctx.expr() != null) {
			Expr expr = (Expr)visit(ctx.expr());
			int type = ((WBuiltInTypeSymbol)ctx.expr().exprType).getTypeIndex();
			switch (type) {
				case 0:
					PrintIntStat printIntStat = new PrintIntStat();
					printIntStat.expr = expr;
					return printIntStat;
				case 1:
					PrintFloatStat printFloatStat = new PrintFloatStat();
					printFloatStat.expr = expr;
					return printFloatStat;
				case 2:
					PrintStrStat printStrStat = new PrintStrStat(ctx.expr().exprType.getName());
					printStrStat.expr = expr;
					if (!isTemporySymbol(ctx.expr().getText())) {
						return printStrStat;
					}
					else {
						printStrStat.localTemps = (expr).tmpVarDefs;
						for (TmpVarDef t :printStrStat.localTemps) {
							printStrStat.tmpVars.add(t.index);
						}
						return printStrStat;
					}
				case 3:
					PrintVecStat printVecStat = new PrintVecStat(typeNameConvert(ctx.expr().exprType.getName()).toLowerCase());
					printVecStat.expr = expr;
					if (!isTemporySymbol(ctx.expr().getText())) {
						return printVecStat;
					}
					else {
						printVecStat.localTemps = (expr).tmpVarDefs;
						for (TmpVarDef t :printVecStat.localTemps) {
							printVecStat.tmpVars.add(t.index);
						}
						return printVecStat;
					}
			}
		}
		return printStat;
	}

	@Override
	public OutputModelObject visitReturn(@NotNull WichParser.ReturnContext ctx) {
		ReturnStat returnStat = new ReturnStat();
		returnStat.rExpr = (Expr)visit(ctx.expr());
		returnStat.localTemps = (returnStat.rExpr).tmpVarDefs;
		for(TmpVarDef t:returnStat.localTemps){
			returnStat.tmpVars.add(t.index);
		}
		if (isHeapObject(ctx.expr().exprType) && ctx.expr() instanceof WichParser.OpContext ) {
			returnStat.tmpIndex = ((OpFunCall)returnStat.rExpr).localTmp;
		}
		if (isHeapObject(ctx.expr().exprType) && ctx.expr() instanceof WichParser.CallContext) {
			returnStat.tmpIndex = ((FuncCall)returnStat.rExpr).localTmp;
		}
		return returnStat;
	}

	@Override
	public OutputModelObject visitBlockStatement(@NotNull WichParser.BlockStatementContext ctx) {
		BlockStat blockStat = new BlockStat();
		blockStat.block = (Block)visit(ctx.block());
		return blockStat;
	}

	@Override
	public OutputModelObject visitOp(@NotNull WichParser.OpContext ctx) {
		if (ctx.exprType instanceof WVector ||ctx.exprType instanceof WString) {
			OpFunCall fc = new OpFunCall(typeNameConvert(ctx.exprType.getName()) +"_"+getOperatorName(ctx));
			List<WichParser.ExprContext> exprs = ctx.expr();
			for (WichParser.ExprContext e: exprs) {
				Expr expr = (Expr)visit(e);
				fc.args.add(expr);
				for(TmpVarDef tmp:expr.tmpVarDefs) {
					fc.tmpVarDefs.add(tmp);
				}
			}
			if (isTempVarNeeded(ctx.getParent())) {
				TmpVarDef t = new TmpVarDef(getTmpIndex(),typeNameConvert(ctx.exprType.getName()));
				fc.localTmp = t.index;
				fc.tmpVarDefs.add(t);
			}
			return fc;
		}else {
			OpExpr opExpr = new OpExpr(ctx.operator().getText());
			opExpr.lExp = (Expr)visit(ctx.expr(0));
			opExpr.rExp = (Expr)visit(ctx.expr(1));
			return opExpr;
		}
	}

	private String getOperatorName(@NotNull WichParser.OpContext ctx) {
		char op= ctx.operator().getText().charAt(0);
		switch (op) {
			case '+':
				return "add";
			case '-':
				return "sub";
			case '*':
				return "mul";
			case '/':
				return "div";
		}
		return null;
	}

	@Override
	public OutputModelObject visitNegate(@NotNull WichParser.NegateContext ctx) {
		NegateExpr negateExpr = new NegateExpr();
		negateExpr.negateExpr = (Expr)visit(ctx.expr());
		return negateExpr;
	}

	@Override
	public OutputModelObject visitNot(@NotNull WichParser.NotContext ctx) {
		NotExpr notExpr = new NotExpr();
		notExpr.notExpr = (Expr)visit(ctx.expr());
		return notExpr;
	}

	@Override
	public OutputModelObject visitCall(@NotNull WichParser.CallContext ctx) {
		String funcName =ctx.call_expr().ID().getText();
		FuncCall fc = new FuncCall(funcName);
		Type type = null;
		if (((WFunctionSymbol) currentScope.resolve(funcName)).getType()!= null) {
			type = ((WFunctionSymbol) currentScope.resolve(funcName)).getType();
			fc.reType = type.getName();
		}
		if(ctx.call_expr().expr_list() != null){
			List<WichParser.ExprContext> exprs = ctx.call_expr().expr_list().expr();
			for (WichParser.ExprContext e : exprs) {
				Expr expr = (Expr)visit(e);
				fc.args.add(expr);
				for (TmpVarDef t : expr.tmpVarDefs) {
					fc.tmpVarDefs.add(t);
				}
			}
		}
		if (type != null && isTempVarNeeded(ctx.getParent()) && isHeapObject(type)) {
			TmpVarDef t = new TmpVarDef(getTmpIndex(), typeNameConvert(fc.reType));
			fc.localTmp = t.index;
			fc.tmpVarDefs.add(t);
		}
		return fc;
	}

	@Override
	public OutputModelObject visitIndex(@NotNull WichParser.IndexContext ctx) {
		String symbolName = ctx.ID().getText();
		Symbol s = currentScope.resolve(symbolName);
		if (s instanceof WVector) {
			VecIndexExpr vecIndexExpr = new VecIndexExpr(symbolName);
			vecIndexExpr.expr = (Expr)visit(ctx.expr());
			return vecIndexExpr;
		}
		else {
			StrIndexExpr strIndexExpr = new StrIndexExpr(symbolName);
			strIndexExpr.expr = (Expr)visit(ctx.expr());
			StrToCharFunCall strToCharFunCall = new StrToCharFunCall();
			strToCharFunCall.arg = strIndexExpr;
			if (isTempVarNeeded(ctx.getParent())) {
				TmpVarDef t = new TmpVarDef(getTmpIndex(),typeNameConvert(SymbolTable._string.getName()));
				strToCharFunCall.localTmp = t.index;
				strToCharFunCall.tmpVarDefs.add(t);
			}
			return strToCharFunCall;
		}
	}

	@Override
	public OutputModelObject visitParens(@NotNull WichParser.ParensContext ctx) {
		ParensExpr parensExpr = new ParensExpr();
		parensExpr.expression = (Expr)visit(ctx.expr());
		parensExpr.tmpVarDefs = (parensExpr.expression).tmpVarDefs;
		return parensExpr;
	}

	@Override
	public OutputModelObject visitAtom(@NotNull WichParser.AtomContext ctx) {
		AtomExpr atomExpr = new AtomExpr();
		atomExpr.primaryExpr = (Expr)visit(ctx.primary());
		atomExpr.tmpVarDefs = (atomExpr.primaryExpr).tmpVarDefs;
			return atomExpr;
	}

	@Override
	public OutputModelObject visitIdentifier(@NotNull WichParser.IdentifierContext ctx) {
		PrimaryExpr primaryExpr = new PrimaryExpr(ctx.getText());
		return primaryExpr;
	}

	@Override
	public OutputModelObject visitString(@NotNull WichParser.StringContext ctx) {
		StringNewFunCall s = new StringNewFunCall(ctx.getText());
		if (isTempVarNeeded(ctx.getParent().getParent())) {
			TmpVarDef t = new TmpVarDef(getTmpIndex(),typeNameConvert(SymbolTable._string.getName()));
			s.localTmp = t.index;
			s.tmpVarDefs.add(t);
		}
		return s;
	}

	@Override
	public OutputModelObject visitVector(@NotNull WichParser.VectorContext ctx) {
		VectorNewFunCall v = new VectorNewFunCall(ctx.expr_list().expr().size());
		for (WichParser.ExprContext e: ctx.expr_list().expr()){
			v.args.add((Expr)visit(e));
		}
		if (isTempVarNeeded(ctx.getParent().getParent())) {
			TmpVarDef t = new TmpVarDef(getTmpIndex(),typeNameConvert(SymbolTable._vector.getName()));
			v.localTmp = t.index;
			v.tmpVarDefs.add(t);
		}
		return v;
	}

	private boolean isTempVarNeeded(ParserRuleContext s){
		return !(s instanceof WichParser.VarDefContext || s instanceof WichParser.AssignContext || s instanceof WichParser.CallStatementContext);
	}

	@Override
	public OutputModelObject visitInteger(@NotNull WichParser.IntegerContext ctx) {
		PrimaryExpr primaryExpr = new PrimaryExpr(ctx.getText());
		return primaryExpr;
	}

	@Override
	public OutputModelObject visitFloat(@NotNull WichParser.FloatContext ctx) {
		PrimaryExpr primaryExpr = new PrimaryExpr(ctx.getText());
		return primaryExpr;
	}

	private boolean isTemporySymbol(String s) {
		if (currentScope.resolve(s) != null) return false;
		else return true;
	}

	private String typeNameConvert(String origin){
		if (origin.equals("string")) {
			return "String";
		}
		else if(origin.equals("[]")) {
			return "Vector";
		}
		else {
			return origin;
		}
	}

	private void pushScope(Scope s) {currentScope = s;}

	private void popScope() {currentScope = currentScope.getEnclosingScope();}
}
