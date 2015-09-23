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
import org.antlr.v4.runtime.misc.NotNull;
import wich.parser.WichBaseListener;
import wich.parser.WichParser;
import wich.semantics.type.WArgSymbol;
import wich.semantics.type.WBlock;
import wich.semantics.type.WFunctionSymbol;
import wich.semantics.type.WVariableSymbol;

public class SymbolTableConstructor extends WichBaseListener {
	private Scope currentScope;
	private static int numOfBlocks;

	public SymbolTableConstructor(SymbolTable symtab) {
		pushScope(symtab.getGlobalScope());
	}

	@Override
	public void enterVarDef(WichParser.VarDefContext ctx) {
		currentScope.define(new WVariableSymbol(ctx.ID().getText()));
	}

	@Override
	public void enterFormal_arg(@NotNull WichParser.Formal_argContext ctx) {
		WArgSymbol arg = new WArgSymbol(ctx.ID().getText());
		currentScope.define(arg); // type set after type computation phase
	}

	@Override
	public void enterFunction(@NotNull WichParser.FunctionContext ctx) {
		WFunctionSymbol f = new WFunctionSymbol(ctx.ID().getText());
		f.setEnclosingScope(currentScope);
		ctx.scope = f;
		currentScope.define(f);
		pushScope(f);
	}

	@Override
	public void exitFunction(@NotNull WichParser.FunctionContext ctx) {
		popScope();
	}

	@Override
	public void enterBlock(@NotNull WichParser.BlockContext ctx) {
		ctx.scope = new WBlock(currentScope, numOfBlocks);
		pushScope(ctx.scope);
		numOfBlocks++;
	}

	@Override
	public void exitBlock(@NotNull WichParser.BlockContext ctx) {
		popScope();
	}

	@Override
	public void exitScript(@NotNull WichParser.ScriptContext ctx) {
		popScope(); // pop off the global scope set in the constructor
	}

	private void pushScope(Scope s) {
		currentScope = s;
	}

	private void popScope() {
		if (currentScope != null) {
			currentScope = currentScope.getEnclosingScope();
		}
	}
}
