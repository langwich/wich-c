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
package wich.parser;


import org.antlr.symtab.Scope;
import org.antlr.symtab.Symbol;
import org.antlr.symtab.TypedSymbol;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import wich.parser.WichParser.ExprContext;
import wich.semantics.SymbolTable;
import wich.semantics.TypeHelper;
import wich.semantics.type.*;


public class TypeAnnotator extends WichBaseListener {

	private final SymbolTable symtab;
	private Scope currentScope;

	public TypeAnnotator(SymbolTable symtab) {
		this.symtab = symtab;
	}

	@Override
	public void exitOp(@NotNull WichParser.OpContext ctx) {
		int op = ctx.operator().start.getType();
		ExprContext lExpr = ctx.expr(0);
		ExprContext rExpr = ctx.expr(1);
		ctx.exprType = SymbolTable.op(op, lExpr, rExpr);
	}

	@Override
	public void exitNegate(@NotNull WichParser.NegateContext ctx) { ctx.exprType = ctx.expr().exprType; }

	@Override
	public void exitNot(@NotNull WichParser.NotContext ctx) {
		//! expr, expr is a boolean
		ctx.exprType = ctx.expr().exprType;
	}

	@Override
	public void exitCall(@NotNull WichParser.CallContext ctx) {
		Symbol s = currentScope.resolve(ctx.call_expr().ID().getText());
		if (s != null)
			ctx.exprType = (WBuiltInTypeSymbol) ((WFunctionSymbol) s).getType();
	}

	@Override
	public void exitIndex(@NotNull WichParser.IndexContext ctx) {
		ctx.exprType = SymbolTable._float;
	}

	@Override
	public void exitParens(@NotNull WichParser.ParensContext ctx) {
		ctx.exprType = ctx.expr().exprType;
	}

	@Override
	public void exitAtom(@NotNull WichParser.AtomContext ctx) {
		WichParser.PrimaryContext primary = ctx.primary();
		//ID
		if (primary.ID() != null) {
			Symbol s = currentScope.resolve(primary.ID().getText());
			if (s != null)
				ctx.exprType = (WBuiltInTypeSymbol) ((TypedSymbol) s).getType();
		}
		//INT
		else if (primary.INT() != null) {
			ctx.exprType = SymbolTable._int;
		}
		//FLOAT
		else if (primary.FLOAT() != null) {
			ctx.exprType = SymbolTable._float;
		}
		//STRING
		else if (primary.STRING() != null) {
			ctx.exprType = SymbolTable._string;
		}
		//vector
		else {
			ctx.exprType = SymbolTable._vector;
			//promote element type to fit in a vector
			int targetIndex = SymbolTable._float. getTypeIndex();
			for (ExprContext elem : ctx.primary().expr_list().expr())
				TypeHelper.promote(elem, targetIndex);
		}
	}

	@Override
	public void exitVarDef(@NotNull WichParser.VarDefContext ctx) {
		Symbol var = currentScope.resolve(ctx.ID().getText());
		//type inference
		((TypedSymbol) var).setType(ctx.expr().exprType);
	}

	@Override
	public void enterScript(@NotNull WichParser.ScriptContext ctx) {
		pushScope(ctx.scope);
	}

	@Override
	public void exitScript(@NotNull WichParser.ScriptContext ctx) {
		popScope();
	}

	@Override
	public void enterFunction(@NotNull WichParser.FunctionContext ctx) {
		pushScope(ctx.scope);
	}

	@Override
	public void exitFunction(@NotNull WichParser.FunctionContext ctx) {
		popScope();
	}

	@Override
	public void enterBlock(@NotNull WichParser.BlockContext ctx) {
		pushScope(ctx.scope);
	}

	@Override
	public void exitBlock(@NotNull WichParser.BlockContext ctx) {
		popScope();
	}

	private void pushScope(Scope s) {
		currentScope = s;
	}

	private void popScope() {
		if (currentScope == null) return;
		currentScope = currentScope.getEnclosingScope();
	}
}
