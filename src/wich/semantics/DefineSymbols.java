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

import org.antlr.symtab.Type;
import org.antlr.v4.runtime.misc.NotNull;
import wich.errors.WichErrorHandler;
import wich.parser.WichParser;
import wich.semantics.symbols.WArgSymbol;
import wich.semantics.symbols.WBlock;
import wich.semantics.symbols.WBuiltInTypeSymbol;
import wich.semantics.symbols.WFunctionSymbol;
import wich.semantics.symbols.WVariableSymbol;

import static wich.errors.ErrorType.DUPLICATE_SYMBOL;
import static wich.errors.ErrorType.INVALID_TYPE;

/*Define symbols, annotate explicit type information for function and args.*/
public class DefineSymbols extends CommonWichListener {
	protected SymbolTable symtab;

	/** Total number of non-arg variables defined */
	protected int numOfVars;

	public DefineSymbols(SymbolTable symtab, WichErrorHandler errorHandler) {
		super(errorHandler);
		this.symtab = symtab;
	}

	public int getNumOfVars() {
		return numOfVars;
	}

	@Override
	public void enterVardef(WichParser.VardefContext ctx) {
		String varName = ctx.ID().getText();
		try {

			currentScope.define(new WVariableSymbol(varName)); // type set in type computation phase
			numOfVars++;
		}catch (IllegalArgumentException e) {
			error(ctx.start, DUPLICATE_SYMBOL, varName);
		}
	}

	@Override
	public void enterFormal_arg(@NotNull WichParser.Formal_argContext ctx) {
		WArgSymbol arg = new WArgSymbol(ctx.ID().getText());
		String typeName = ctx.type().getText();
		WBuiltInTypeSymbol type = resolveType(typeName);
		if ( type!=null ) {
			arg.setType(type);
			((WFunctionSymbol)currentScope).argTypes.add(type);
		}
		else{
			error(ctx.ID().getSymbol(), INVALID_TYPE, arg.getName());
		}
		currentScope.define(arg);
	}

	@Override
	public void enterFunction(@NotNull WichParser.FunctionContext ctx) {
		WFunctionSymbol f = new WFunctionSymbol(ctx.ID().getText());
		f.setEnclosingScope(currentScope);
		// resolve return type of the method since it's explicit
		if ( ctx.type()==null ) {
			f.setType(SymbolTable._void);
		}
		else {
			String typeName = ctx.type().getText();
			Type type = resolveType(typeName);
			if ( type!=null ) f.setType(type);
			else error(ctx.ID().getSymbol(), INVALID_TYPE, f.getName());
		}
		ctx.scope = f;
		currentScope.define(f);
		//symtab.functions.put(ctx.ID().getText(),f);
		pushScope(f);
	}

	@Override
	public void exitFunction(@NotNull WichParser.FunctionContext ctx) { popScope(); }

	@Override
	public void enterBlock(@NotNull WichParser.BlockContext ctx) {
		WBlock blk = new WBlock(currentScope);
		if ( currentScope instanceof WBlock ) {
			((WBlock)currentScope).addNestedBlock(blk);
		}
		else if ( currentScope instanceof WFunctionSymbol ) {
			((WFunctionSymbol)currentScope).block = blk;
		}
		ctx.scope = blk;
		pushScope(ctx.scope);
	}

	@Override
	public void exitBlock(@NotNull WichParser.BlockContext ctx) {
		popScope();
	}

	@Override
	public void enterScript(@NotNull WichParser.ScriptContext ctx) {
		ctx.scope = symtab.getGlobalScope();
		pushScope(ctx.scope);
	}

	@Override
	public void exitScript(@NotNull WichParser.ScriptContext ctx) {
		popScope(); // pop off the global scope set in the constructor
	}
}
