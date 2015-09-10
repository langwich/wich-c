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

import org.antlr.symtab.GlobalScope;
import org.antlr.symtab.LocalScope;
import org.antlr.symtab.Scope;
import org.antlr.symtab.Type;
import org.antlr.v4.runtime.misc.NotNull;
import wich.semantics.SymbolTable;
import wich.semantics.type.WArgSymbol;
import wich.semantics.type.WFunction;
import wich.semantics.type.WVariableSymbol;

public class SymbolTableConstructor extends WichBaseListener{

	private final SymbolTable symtab;
	private Scope currentScope;

	public SymbolTableConstructor(SymbolTable symtab) {
		this.symtab = symtab;
		this.currentScope = symtab.getGlobalScope();
	}

	@Override
	public void enterVarDef(WichParser.VarDefContext ctx) {
        currentScope.define(new WVariableSymbol(ctx.ID().getText()));
	}

    @Override
    public void enterFormal_arg(@NotNull WichParser.Formal_argContext ctx) {
        currentScope.define(new WArgSymbol(ctx.ID().getText()));
    }

    @Override
    public void enterFunction(@NotNull WichParser.FunctionContext ctx) {
        WFunction f = new WFunction(ctx.ID().getText());
        ctx.scope = f;
        f.setEnclosingScope(currentScope);
        currentScope.define(f);
        //resolve return type of the method
        if(ctx.type() != null)
            f.setType((Type)currentScope.resolve(ctx.type().getText()));
        else
            f.setType(null);
        pushScope(f);
    }

    @Override
    public void exitFunction(@NotNull WichParser.FunctionContext ctx) {
        popScope();
    }

    @Override
    public void enterBlock(@NotNull WichParser.BlockContext ctx) {
        LocalScope l = new LocalScope(currentScope);
        ctx.scope = l;
        pushScope(l);
    }

    @Override
    public void exitBlock(@NotNull WichParser.BlockContext ctx) {
        popScope();
    }

    @Override
    public void enterScript(@NotNull WichParser.ScriptContext ctx) {
        ctx.scope = (GlobalScope) currentScope;
    }

    @Override
    public void exitScript(@NotNull WichParser.ScriptContext ctx) {
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
