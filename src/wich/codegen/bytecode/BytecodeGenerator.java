package wich.codegen.bytecode;

import org.antlr.symtab.Scope;
import org.antlr.symtab.StringTable;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;
import wich.parser.WichBaseVisitor;
import wich.parser.WichParser;
import wich.semantics.SymbolTable;
import wich.semantics.symbols.WFunctionSymbol;
import wich.semantics.symbols.WVariableSymbol;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  In the end of visitor is a better alternative for generating code
 *  over the listener. The listener can't control the order in which the
 *  children are visited which makes it really challenging to generateObjectFile code for
 *  an if-statement where we need to generateObjectFile BRF in between the condition
 *  and the statement. I started down the path of attaching code to each node but
 *  it got very messy and could be inefficient as I made lots and lots of copies
 *  of code blocks so that each node had the generated code for it. Using
 *  a visitor means that the temporary code blocks go away and are not stored
 *  in the tree. Naturally, we can add it to interesting nodes like statements
 *  so that we can generate a nice intermixed source and byte code assembly
 *  for debugging purposes.
 *
 *  If I am going to compute and store code for each node, the negative of
 *  visitors, having to manually visit children, is overcome.  Oh, the basevisitor
 *  gen'd class actually does that for me. It makes sure we visit everything.
 */
public class BytecodeGenerator extends WichBaseVisitor<Code> {
	//	public Tool tool;
	public SymbolTable symtab;
	public Scope currentScope;

	public ASM asm;
	Map<String, Code> functionBodies = new LinkedHashMap<String, Code>();
	Code globalInitCode = Code.None; // stick at start of main

	public BytecodeGenerator(SymbolTable symtab) {
		this.symtab = symtab;
		asm = new ASM(symtab);
		currentScope = symtab.GLOBALS;
	}

	/** This and defaultResult() critical to getting code to bubble up the
	 *  visitor call stack when we don't implement every method.
	 */
	@Override
	protected Code aggregateResult(Code aggregate, Code nextResult) {
		if ( aggregate!=Code.None ) {
			if ( nextResult!=Code.None ) {
				return aggregate.join(nextResult);
			}
			return aggregate;
		}
		else {
			return nextResult;
		}
	}

	@Override
	protected Code defaultResult() {
		return Code.None;
	}

	public Code visit(@NotNull WichParser.ScriptContext ctx) {
		Code funcs = Code.None;
		for (WichParser.FunctionContext f: ctx.function()) {
			funcs.join(visit(f));
		}

		WFunctionSymbol f = new WFunctionSymbol("main");
		f.setType(SymbolTable._void);
		symtab.functions.put(f.getName(),f);
		Code main = Code.None;
		for (WichParser.StatementContext s : ctx.statement()){
			main = main.join(visit(s));
		}
		main = main.join(asm.halt());
		main = globalInitCode.join(main);
		functionBodies.put("main",main);
		funcs.join(main);
		return funcs;
	}

	@Override
	public Code visitFunction(@NotNull WichParser.FunctionContext ctx) {
		pushScope(ctx.scope);
		Code func = visit(ctx.block());
		String funcName = ctx.ID().getText();
		functionBodies.put(funcName, func);
		symtab.functions.put(funcName,(WFunctionSymbol)currentScope.resolve(funcName));
		popScope();
		return Code.None; // don't hook onto other bodies
	}

	@Override
	public Code visitBlock(@NotNull WichParser.BlockContext ctx) {
		pushScope(ctx.scope);
		Code blk = Code.None;
		for (WichParser.StatementContext s : ctx.statement()) {
			blk = blk.join(visit(s));
		}
		blk = blk.join(asm.ret());
		popScope();
		return blk;
	}

	@Override
	public Code visitVarDefStatement(@NotNull WichParser.VarDefStatementContext ctx) {
		return visit(ctx.vardef());
	}

	@Override
	public Code visitAssign(@NotNull WichParser.AssignContext ctx) {
		Code code = visit(ctx.expr());
		WVariableSymbol v = (WVariableSymbol)currentScope.resolve(ctx.ID().getText());
		Instr store;
		if ( currentScope ==symtab.GLOBALS ) {
			store = asm.store_global(v.getSymbolIndex());
		}
		else {
			store = asm.store(v.getSymbolIndex());
		}
		code = code.join(store);
		return code;
	}

	@Override
	public Code visitVardef(@NotNull WichParser.VardefContext ctx) {
		WVariableSymbol v = (WVariableSymbol)currentScope.resolve(ctx.ID().getText());
		if (currentScope == symtab.GLOBALS) {
			symtab.globals.put(ctx.ID().getText(),v);
            symtab.functions.get("main").define(v);
		}
		Code code = visit(ctx.expr());
		Instr store;
		if ( currentScope == symtab.GLOBALS ) {
			store = asm.store_global(v.getSymbolIndex());
		}
		else {
			store = asm.store(v.getSymbolIndex());
		}
		code = code.join(store);
		return code;
	}

	@Override
	public Code visitAtom(@NotNull WichParser.AtomContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Code visitInteger(@NotNull WichParser.IntegerContext ctx) {
		Code code = Code.None;
		code = code.join(asm.iconst(Integer.valueOf(ctx.INT().getText())));
		return code;
	}

	@Override
	public Code visitString(@NotNull WichParser.StringContext ctx) {
		Code code = Code.None;
		int index = symtab.defineStringLiteral(ctx.STRING().getText());
		code = code.join(asm.sconst(index));
		return code;
	}

    @Override
    public Code visitVector(@NotNull WichParser.VectorContext ctx) {
        Code code = Code.None;
        code = code.join(visit(ctx.expr_list()));
        code = code.join(asm.iconst(ctx.expr_list().expr().size()));
        code = code.join(asm.vector());
        return code;
    }

    @Override
    public Code visitExpr_list(@NotNull WichParser.Expr_listContext ctx) {
        Code code = Code.None;
        List<WichParser.ExprContext> e = ctx.expr();
        for (int i =e.size()-1; i>=0; i--) {
            code = code.join(visit(e.get(i)));
        }
        return code;
    }

	public void pushScope(Scope scope) {currentScope = scope;}

	public void popScope() {currentScope = currentScope.getEnclosingScope();}

}
