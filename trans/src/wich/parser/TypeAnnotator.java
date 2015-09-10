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
import org.antlr.v4.runtime.misc.NotNull;
import wich.semantics.SymbolTable;
import wich.semantics.type.*;


public class TypeAnnotator extends WichBaseListener {

	private final SymbolTable symtab;
    private Scope currentScope;

	public TypeAnnotator(SymbolTable symtab) {
        this.symtab = symtab;
	}

    @Override
    public void exitOp(@NotNull WichParser.OpContext ctx) {
        //TODO type promotion
        super.exitOp(ctx);
    }

    @Override
    public void exitNegate(@NotNull WichParser.NegateContext ctx) {
        ctx.exprType = ctx.expr().exprType;
    }

    @Override
    public void exitNot(@NotNull WichParser.NotContext ctx) {
        //Not sure what it means, used in conditional to see if it's nonzero? Like a boolean?
        ctx.exprType = ctx.expr().exprType;
    }

    @Override
    public void exitCall(@NotNull WichParser.CallContext ctx) {
        Symbol s = currentScope.resolve(ctx.call_expr().ID().getText());
        if(s != null)
            ctx.exprType = ((WFunction)s).getType();
    }

    @Override
    public void exitIndex(@NotNull WichParser.IndexContext ctx) {
        ctx.exprType = WFloat.instance();
    }

    @Override
    public void exitParens(@NotNull WichParser.ParensContext ctx) {
        ctx.exprType = ctx.expr().exprType;
    }

    @Override
    public void exitAtom(@NotNull WichParser.AtomContext ctx) {
        WichParser.PrimaryContext primary = ctx.primary();
        //ID
        if(primary.ID() != null){
            Symbol s = currentScope.resolve(primary.ID().getText());
            if(s != null)
                ctx.exprType = ((TypedSymbol)s).getType();
        }
        //INT
        else if(primary.INT() != null){
            ctx.exprType = WInt.instance();
        }
        //FLOAT
        else if(primary.FLOAT() != null){
            ctx.exprType = WFloat.instance();
        }
        //STRING
        else if(primary.STRING() !=null){
            ctx.exprType = WString.instance();
        }
        //vector
        else{
            ctx.exprType = WVector.instance();
        }
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
