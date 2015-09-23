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
package wich.semantics;

import org.antlr.symtab.Scope;
import org.antlr.symtab.Symbol;
import org.antlr.symtab.Type;
import org.antlr.v4.runtime.misc.NotNull;
import wich.parser.WichBaseListener;
import wich.parser.WichParser;
import wich.semantics.symbols.WBuiltInTypeSymbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypeChecker extends WichBaseListener {
	private final SymbolTable symtab;
	private Scope currentScope;
	public final List<String> errors = new ArrayList<>();

	public TypeChecker(SymbolTable symtab) {
		this.symtab = symtab;
	}

	@Override
	public void exitAssign(@NotNull WichParser.AssignContext ctx) {
		Symbol s = currentScope.resolve(ctx.ID().getText());
		Type left = (WBuiltInTypeSymbol) s;
		Type right = ctx.expr().exprType;

		if (TypeHelper.isLegalAssign(left, right))
			return;
		else
			error("Incompatible type in assignment.", new Exception());
	}

	@Override
	public void exitElementAssign(@NotNull WichParser.ElementAssignContext ctx) {

		WichParser.ExprContext index = ctx.expr(0);
		WichParser.ExprContext elem = ctx.expr(1);

		//index must be expression of int type
		if (index.exprType != SymbolTable._int)
			error("Invalid vector index type.", new Exception());

		//element value must be expression of float type or can be promoted to float in equality
		if (elem.exprType != SymbolTable._float && elem.promoteToType != SymbolTable._float)
			error("Invalid vector element.", new Exception());
	}

	@Override
	public void exitVector(@NotNull WichParser.VectorContext ctx) {
		if (ctx.expr_list() != null){
			for (WichParser.ExprContext elem : ctx.expr_list().expr()){
				if (elem.exprType != SymbolTable._float ||
						elem.promoteToType != SymbolTable._float)
					error("Invalid vector element.", new Exception());
			}
		}
	}

//	@Override
//	public void exitAtom(@NotNull WichParser.AtomContext ctx) {
//		//check vector element type
//		if (ctx.primary().expr_list() != null){
//			for (WichParser.ExprContext elem : ctx.primary().expr_list().expr()){
//				if (elem.exprType != SymbolTable._float ||
//						elem.promoteToType != SymbolTable._float)
//				error("Invalid vector element.", new Exception());
//			}
//		}
//
//	}

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

	// error support for typeChecker
	private void error(String msg) {
		errors.add(msg);
	}

	private void error(String msg, Exception e) {
		errors.add(msg + "\n" + Arrays.toString(e.getStackTrace()));
	}

}
