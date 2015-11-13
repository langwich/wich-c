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

		WFunctionSymbol m = new WFunctionSymbol("main");
		m.setType(SymbolTable._void);
		currentScope.define(m);
		pushScope(m);
		Code main = Code.None;
		for (WichParser.StatementContext s : ctx.statement()){
			main = main.join(visit(s));
		}
		main = main.join(asm.halt());
		main = globalInitCode.join(main);
		functionBodies.put("main",main);
		funcs.join(main);
		popScope();
		return funcs;
	}

	@Override
	public Code visitFunction(@NotNull WichParser.FunctionContext ctx) {
		pushScope(ctx.scope);
		Code func = visit(ctx.block());
		String funcName = ctx.ID().getText();
		functionBodies.put(funcName, func);
		popScope();
		return Code.None; // don't hook onto other bodies
	}

	@Override
	public Code visitBlock(@NotNull WichParser.BlockContext ctx) {
		if (ctx.scope.getEnclosingScope() == symtab.GLOBALS) {
			ctx.scope.setEnclosingScope(currentScope);
			((WFunctionSymbol)currentScope).block = (WBlock)ctx.scope;
		}
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
	public Code visitVardef(@NotNull WichParser.VardefContext ctx) {
		WVariableSymbol v = (WVariableSymbol)currentScope.resolve(ctx.ID().getText());
		if (v.getScope() == symtab.GLOBALS) {
			symtab.getfunctions().get("main").define(v);
		}
		Code code = visit(ctx.expr());
//		if (v.getType() != symtab._vector) {
////			Instr store;
////			if (currentScope == symtab.GLOBALS || currentScope.getEnclosingScope() == symtab.GLOBALS) {
////				store = asm.store(v.getInsertionOrderNumber());
////			} else {
////				store = asm.store( v.getInsertionOrderNumber());
////			}
//			code = code.join(asm.store(getSymbolIndex(v)));
//		}
		code = code.join(asm.store(getSymbolIndex(v)));
		return code;
	}

	@Override
	public Code visitAssign(@NotNull WichParser.AssignContext ctx) {
		Code code = visit(ctx.expr());
		WVariableSymbol v = (WVariableSymbol)currentScope.resolve(ctx.ID().getText());
		//code = code.join(asm.store(v.getInsertionOrderNumber()));
		code = code.join(asm.store(getSymbolIndex(v)));
		return code;
	}

	@Override
	public Code visitElementAssign(@NotNull WichParser.ElementAssignContext ctx) {
		WVariableSymbol v = (WVariableSymbol)currentScope.resolve(ctx.ID().getText());
		Code code = CodeBlock.join(asm.vload(getSymbolIndex(v)),visit(ctx.expr(0)), visit(ctx.expr(1)));
		if (ctx.expr(1).exprType == SymbolTable._int) {
			code = code.join(asm.i2f());
		}
		code = code.join(asm.store_index());
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
		int i =  currentScope.resolve(ctx.ID().getText()).getInsertionOrderNumber();
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
			if (type instanceof WInt) {
				code = code.join(asm.iprint());
			}
			else if(type instanceof WFloat) {
				code = code.join(asm.fprint());
			}
			else if(type instanceof WString) {
				code = code.join(asm.sprint());
			}
			else if(type instanceof WVector) {
				code = code.join(asm.vprint());
			}
			else {
				code = code.join(asm.bprint());
			}
		}
		return code;
	}

	@Override
	public Code visitBlockStatement(@NotNull WichParser.BlockStatementContext ctx) {
		return visit(ctx.block());
	}

	private Type getExprType(@NotNull WichParser.ExprContext ctx) {
		return ctx.exprType;
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
	public Code visitFloat(@NotNull WichParser.FloatContext ctx) {
		Code code = Code.None;
		code = code.join(asm.fconst(Float.valueOf(ctx.FLOAT().getText())));
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
			//int index = s.getInsertionOrderNumber();
			int index = getSymbolIndex(s);
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
		if (ctx.getParent() instanceof WichParser.OpContext &&
				((WichParser.OpContext) ctx.getParent()).exprType == SymbolTable._string) {
			code = code.join(asm.v2s());
		}
//		else if (ctx.getParent().getParent() instanceof WichParser.VardefContext) {
//			Symbol vector = currentScope.resolve(((WichParser.VardefContext) ctx.getParent().getParent()).ID().getText());
//			code = code.join(asm.store(getSymbolIndex(vector)));
//		}
		return code;
	}


	@Override
	public Code visitExpr_list(@NotNull WichParser.Expr_listContext ctx) {
		Code code = Code.None;
		List<WichParser.ExprContext> list = ctx.expr();
		if(ctx.getParent() instanceof WichParser.VectorContext) {
			for(int i = 0; i < list.size(); i++) {  // push onto stack in reverse order
				code = code.join(visit(list.get(i)));
				if(list.get(i).exprType == SymbolTable._int) {
					code = code.join(asm.i2f());
				}
			}
		}
		else {
			for(int i = 0; i < list.size(); i++) {
				code = code.join(visit(list.get(i)));
			}
		}
		return code;
	}

	private Symbol getVectorSymbol(@NotNull WichParser.Expr_listContext ctx, String text) {
		return currentScope.resolve(text);
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
			return CodeBlock.join(cond, asm.brf(stat.sizeBytes()+asm.brf().size+asm.br().size),
					stat,asm.br(stat2.sizeBytes() + asm.br().size), stat2);
		}
	}

	@Override
	public Code visitOp(@NotNull WichParser.OpContext ctx) {
		Code left = visit(ctx.expr(0));
		Code right = visit(ctx.expr(1));
		Code op = visit(ctx.operator());

		//type promotion
		if (ctx.expr(0).exprType != ctx.expr(1).exprType) {
			//promote to string: string op(add) with int, float, vector
			if (ctx.exprType == SymbolTable._string) {
				if (ctx.expr(0).exprType != SymbolTable._string) {
					left = promote2S(left, ctx.expr(0).exprType);
				}
				else if (ctx.expr(1).exprType != SymbolTable._string) {
					right = promote2S(right, ctx.expr(1).exprType);
				}
			}
			//promote int to float: with vector or with float
			else if(ctx.exprType == SymbolTable._float) {
				if (ctx.expr(0).exprType == SymbolTable._int) {
					left = promoteI2F(left);
				}
				else if (ctx.expr(1).exprType == SymbolTable._int) {
					right = promoteI2F(right);
				}
			}
		}
		//order of operands in vector operations, vector first
		if(ctx.exprType == SymbolTable._vector && ctx.expr(0).exprType != SymbolTable._vector) {
			return CodeBlock.join(right, left, op);
		}
		else {
			return CodeBlock.join(left, right,op);
		}
	}

	public Code promote2S(Code code, Type type){
		if (type == SymbolTable._int) {
			code = code.join(asm.i2s());
		}
		else if (type == SymbolTable._float) {
			code = code.join(asm.f2s());
		}
		else if (type == SymbolTable._vector) {
			code = code.join(asm.v2s());
		}
		return code;
	}
	public Code promoteI2F(Code code){
		code = code.join(asm.i2f());
		return code;
	}

	@Override
	public Code visitNegate(@NotNull WichParser.NegateContext ctx) {
		Code code = visit(ctx.expr());
		Type type = ctx.exprType != null ? ctx.exprType : ctx.promoteToType;
		if (type instanceof WInt) {
			code = code.join(asm.ineg());
		}
		else {
			code = code.join(asm.fneg());
		}
		return code;
	}

	@Override
	public Code visitNot(@NotNull WichParser.NotContext ctx) {
		Code code = visit(ctx.expr());
		code = code.join(asm.not());
		return code;
	}

	@Override
	public Code visitIndex(@NotNull WichParser.IndexContext ctx) {
		Code code = Code.None;
		WVariableSymbol var = (WVariableSymbol)currentScope.resolve(ctx.ID().getText());
		if (var.getType() == symtab._vector) {
			code = CodeBlock.join(asm.vload(getSymbolIndex(var)),visit(ctx.expr()),asm.vload_index());
		}
		else {
			code = CodeBlock.join(asm.sload(getSymbolIndex(var)),visit(ctx.expr()),asm.sload_index());
		}
		return code;
	}

	@Override
	public Code visitOperator(@NotNull WichParser.OperatorContext ctx) {
		Code op = Code.None;
		Type type = getExprType(((WichParser.OpContext) ctx.getParent()));
		WichParser.OpContext expr = (WichParser.OpContext) ctx.getParent();
		WichParser.ExprContext left = expr.expr(0);
		WichParser.ExprContext right = expr.expr(1);
		//vector operations
		if (type == SymbolTable._vector) {
			//both operands are vectors
			if(left.exprType == SymbolTable._vector && right.exprType == SymbolTable._vector) {
				op = vOpV(ctx, op);
			}
			// only one operand is vector
			else {
				//vector op int
				if(left.exprType == SymbolTable._int || right.exprType == SymbolTable._int) {
					op = vOpI(ctx, op);
				}
				//vector op float
				else if(left.exprType == SymbolTable._float || right.exprType == SymbolTable._float) {
					op = vOpF(ctx, op);
				}
			}
		}
		// boolean operation
		else if (type == SymbolTable._boolean) {
			op = compareOp(ctx, op, left, right);
		}
		//simple arithmetic and string add
		else {
			op = arithmeticOp(ctx, op, type);
		}
		return op;
	}

	private Code arithmeticOp(WichParser.OperatorContext ctx, Code op, Type type) {
		if(ctx.ADD() != null) {
			if (type == SymbolTable._string) {
				op = op.join(asm.sadd());
			} else {
				op = type == SymbolTable._int ? op.join(asm.iadd()) : op.join(asm.fadd());
			}
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
		return op;
	}

	private Code compareOp(WichParser.OperatorContext ctx, Code op, WichParser.ExprContext left, WichParser.ExprContext right) {
		if(ctx.GE() != null) {
			if (left.exprType == SymbolTable._string && right.exprType == SymbolTable._string) {
				op = op.join(asm.sge());
			} else {
				op = (left.exprType == SymbolTable._float || right.exprType == SymbolTable._float )?
						op.join(asm.fge()) : op.join(asm.ige());
			}
		}
		else if(ctx.GT() != null) {
			if (left.exprType == SymbolTable._string && right.exprType == SymbolTable._string) {
				op = op.join(asm.sgt());
			} else {
				op = (left.exprType == SymbolTable._float || right.exprType == SymbolTable._float )?
						op.join(asm.fgt()) : op.join(asm.igt());
			}
		}
		else if(ctx.LE() != null) {
			if (left.exprType == SymbolTable._string && right.exprType == SymbolTable._string) {
				op = op.join(asm.sle());
			} else {
				op = (left.exprType == SymbolTable._float || right.exprType == SymbolTable._float )?
						op.join(asm.fle()) : op.join(asm.ile());
			}
		}
		else if(ctx.LT() != null) {
			if (left.exprType == SymbolTable._string && right.exprType == SymbolTable._string) {
				op = op.join(asm.slt());
			} else {
				op = (left.exprType == SymbolTable._float || right.exprType == SymbolTable._float )?
						op.join(asm.flt()) : op.join(asm.ilt());
			}
		}
		else if(ctx.EQUAL_EQUAL() != null) {
			if (left.exprType == SymbolTable._string && right.exprType == SymbolTable._string) {
				op = op.join(asm.seq());
			} else {
				op = (left.exprType == SymbolTable._float || right.exprType == SymbolTable._float )?
						op.join(asm.feq()) : op.join(asm.ieq());
			}
		}
		else if(ctx.NOT_EQUAL() != null) {
			if (left.exprType == SymbolTable._string && right.exprType == SymbolTable._string) {
				op = op.join(asm.sneq());
			} else {
				op = (left.exprType == SymbolTable._float || right.exprType == SymbolTable._float )?
						op.join(asm.fneq()) : op.join(asm.ineq());
			}
		}
		else if(ctx.OR() != null) {
			op = op.join(asm.or());
		}
		return op;
	}

	private Code vOpF(WichParser.OperatorContext ctx, Code op) {
		if(ctx.ADD() != null) {
			op = op.join(asm.vaddf());
		}
		else if(ctx.SUB() != null) {
			op = op.join(asm.vsubf());
		}
		else if(ctx.MUL() != null) {
			op = op.join(asm.vmulf());
		}
		else if(ctx.DIV() != null) {
			op = op.join(asm.vdivf());
		}
		return op;
	}

	private Code vOpI(WichParser.OperatorContext ctx, Code op) {
		if(ctx.ADD() != null) {
			op = op.join(asm.vaddi());
		}
		else if(ctx.SUB() != null) {
			op = op.join(asm.vsubi());
		}
		else if(ctx.MUL() != null) {
			op = op.join(asm.vmuli());
		}
		else if(ctx.DIV() != null) {
			op = op.join(asm.vdivi());
		}
		return op;
	}

	// vector op vector
	private Code vOpV(WichParser.OperatorContext ctx, Code op) {
		if(ctx.ADD() != null) {
			op = op.join(asm.vadd());
		}
		else if(ctx.SUB() != null) {
			op = op.join(asm.vsub());
		}
		else if(ctx.MUL() != null) {
			op = op.join(asm.vmul());
		}
		else if(ctx.DIV() != null) {
			op = op.join(asm.vdiv());
		}
		else if(ctx.EQUAL_EQUAL() != null) {
			op = op.join(asm.veq());
		}
		else if (ctx.NOT_EQUAL() != null) {
			op =  op.join(asm.vneq());
		}
		return op;
	}

	public void pushScope(Scope scope) {currentScope = scope;}

	public void popScope() {currentScope = currentScope.getEnclosingScope();}

	public int getSymbolIndex(Symbol s) {
		int index = s.getInsertionOrderNumber();
		if (s.getScope() instanceof WFunctionSymbol || s.getScope() == symtab.GLOBALS) {
			return index;
		}
		Scope enScope = s.getScope().getEnclosingScope();
		while (!(enScope instanceof WFunctionSymbol || enScope == symtab.GLOBALS)){
			index += enScope.getAllSymbols().size();
			enScope = enScope.getEnclosingScope();
		}
		index += enScope.getAllSymbols().size();
		return index;
	}
}
