package wich.codegen.bytecode;

import org.antlr.symtab.Scope;
import org.antlr.symtab.StringTable;
import org.antlr.symtab.Symbol;
import org.antlr.symtab.Type;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;
import wich.parser.WichBaseVisitor;
import wich.parser.WichParser;
import wich.semantics.SymbolTable;
import wich.semantics.symbols.*;

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
		symtab.functions.put(f.getName(), f);
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
		//symtab.functions.put(funcName,(WFunctionSymbol)currentScope.resolve(funcName));
		//func = func.join(asm.ret());
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
		if (ctx.getParent() instanceof WichParser.FunctionContext) {
			blk = blk.join(asm.ret());
		}
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
			//store = asm.store(((WFunctionSymbol)currentScope.getEnclosingScope()).nargs()+v.getSymbolIndex());
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
			store = asm.store(((WFunctionSymbol)currentScope.getEnclosingScope()).nargs()+v.getSymbolIndex());
		}
		code = code.join(store);
		return code;
	}

	@Override
	public Code visitCallStatement(@NotNull WichParser.CallStatementContext ctx) {
		return visit(ctx.call_expr());
	}

	@Override
	public Code visitCall_expr(@NotNull WichParser.Call_exprContext ctx) {
		Code code = Code.None;
		if(ctx.expr_list() != null) {
			code = code.join(visit(ctx.expr_list()));
		}
		int i = symtab.functions.get(ctx.ID().getText()).getInsertionOrderNumber();
		return code.join(asm.call(i));
	}

	@Override
	public Code visitReturn(@NotNull WichParser.ReturnContext ctx) {
		return visit(ctx.expr()).join(asm.retv());
	}

	@Override
	public Code visitWhile(@NotNull WichParser.WhileContext ctx) {
		Code cond = visit(ctx.expr());
		Code stat = visit(ctx.statement());
		Code all = CodeBlock.join(cond,asm.brf(stat.sizeBytes()+asm.br().size+asm.br().size),stat);
		return all.join(asm.br(-all.sizeBytes()));
	}

	@Override
	public Code visitPrint(@NotNull WichParser.PrintContext ctx) {
		Code code = Code.None;
		if(ctx.expr() != null) {
			code = code.join(visit(ctx.expr()));
			Type type = getExprType(ctx.expr());
			if (type instanceof WInt || type instanceof WBoolean) {
				code = code.join(asm.iprint());
			}
			else if(type instanceof WFloat) {
				code = code.join(asm.fprint());
			}
			else if(type instanceof WString) {
				code = code.join(asm.sprint());
			}
			else {
				code = code.join(asm.vprint());
			}
		}
		return code;
	}

	private Type getExprType(@NotNull WichParser.ExprContext ctx) {
		return ctx.exprType == null ? ctx.promoteToType:ctx.exprType;
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
	public Code visitIdentifier(@NotNull WichParser.IdentifierContext ctx) {
		Symbol symbol = currentScope.resolve(ctx.ID().getText());
		return load(symbol);
	}

	public Code load(Symbol symbol) {
		if ( symbol!=null && symbol instanceof WVariableSymbol) {
			WVariableSymbol s = (WVariableSymbol)symbol;
			int index = s.getSymbolIndex();
			if ( symbol.getScope().getName().equals("main")) {
				return asm.load_global(index);
			}
			else { // must be an arg or local
				if ( s.getType() == SymbolTable._int ) {
					return asm.iload(index);
				}
				else if ( s.getType() == SymbolTable._float ) {
					return asm.fload(index);
				}
				else if ( s.getType() == SymbolTable._boolean ) {
					return asm.iload(index);
				}
				else if ( s.getType() == SymbolTable._string ) {
					return asm.sload(index);
				}
				else {
					return asm.vload(index);
				}
			}
		}
		return Code.None;
	}


		@Override
	public Code visitString(@NotNull WichParser.StringContext ctx) {
		Code code = Code.None;
		int index = symtab.defineStringLiteral(ctx.STRING().getText());
		code = code.join(asm.sconst(index));
		return code;
	}

	@Override
	public Code visitTrueLiteral(@NotNull WichParser.TrueLiteralContext ctx) {
		Code code = Code.None;
		code = code.join(asm.iconst(1));
		return code;
	}

	@Override
	public Code visitFalseLiteral(@NotNull WichParser.FalseLiteralContext ctx) {
		Code code = Code.None;
		code = code.join(asm.iconst(0));
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
		for (int i = 0; i < e.size(); i++) {
			code = code.join(visit(e.get(i)));
		}
		return code;
	}

	@Override
	public Code visitIf(@NotNull WichParser.IfContext ctx) {
		Code cond = visit(ctx.expr());
		Code stat = visit(ctx.statement(0));
		if (ctx.getChildCount() == 5) {
			return CodeBlock.join(cond, asm.brf(stat.sizeBytes()+asm.br().size),stat);
		}
		else {
			Code stat2 = visit(ctx.statement(1));
			return CodeBlock.join(cond, asm.brf(stat.sizeBytes()+stat2.sizeBytes()+asm.br().size+asm.br().size),
					stat,asm.br(stat2.sizeBytes() + asm.br().size), stat2);
		}
	}

	@Override
	public Code visitOp(@NotNull WichParser.OpContext ctx) {
		Code left = visit(ctx.expr(0));
		Code right = visit(ctx.expr(1));
		Code op = visit(ctx.operator());
		return CodeBlock.join(left,right,op);
	}

	@Override
	public Code visitOperator(@NotNull WichParser.OperatorContext ctx) {
		Code op = Code.None; // todo vector,string operator
		Type type = getExprType(((WichParser.OpContext) ctx.getParent()).expr(0));
		if(ctx.ADD() != null) {
			op = type == SymbolTable._int ? op.join(asm.iadd()) : op.join(asm.fadd());
		}
		else if(ctx.SUB() != null) {
			op = type == SymbolTable._int ? op.join(asm.isub()) : op.join(asm.fsub());
		}
		else if(ctx.MUL() != null) {
			op = type == SymbolTable._int ? op.join(asm.imul()) : op.join(asm.fmul());
		}
		else if(ctx.DIV() != null) {
			op = type == SymbolTable._int ? op.join(asm.idiv()) : op.join(asm.fdiv());
		}
		else if(ctx.GE() != null) {
			op = type == SymbolTable._int ? op.join(asm.ige()) : op.join(asm.fge());
		}
		else if(ctx.GT() != null) {
			op = type == SymbolTable._int ? op.join(asm.igt()) : op.join(asm.fgt());
		}
		else if(ctx.LE() != null) {
			op = type == SymbolTable._int ? op.join(asm.ile()) : op.join(asm.fle());
		}
		else if(ctx.LT() != null) {
			op = type == SymbolTable._int ? op.join(asm.ilt()) : op.join(asm.flt());
		}
		else if(ctx.EQUAL_EQUAL() != null) {
			op = type == SymbolTable._int ? op.join(asm.ieq()) : op.join(asm.feq());
		}
		else if(ctx.NOT_EQUAL() != null) {
			op = type == SymbolTable._int ? op.join(asm.ineq()) : op.join(asm.fneq());
		}

		return op;
	}

	public void pushScope(Scope scope) {currentScope = scope;}

	public void popScope() {currentScope = currentScope.getEnclosingScope();}

}
