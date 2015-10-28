package wich.codegen.bytecode;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;
import wich.parser.WichBaseVisitor;
import wich.parser.WichParser;
import wich.semantics.SymbolTable;

import java.util.LinkedHashMap;
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

	public ASM asm;
	Map<String, Code> functionBodies = new LinkedHashMap<String, Code>();
	Code globalInitCode = Code.None; // stick at start of main

	public BytecodeGenerator(SymbolTable symtab) {
		this.symtab = symtab;
		asm = new ASM(symtab);
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

	@Override
	public Code visitMain(@NotNull WichParser.MainContext ctx) {
		Code func = visitChildren(ctx);
		func = func.join(asm.halt());
		func = globalInitCode.join(func);
		functionBodies.put("main", func);
		return Code.None; // don't hook onto other bodies
	}

	@Override
	public Code visitFunction(@NotNull WichParser.FunctionContext ctx) {
		Code blk = visit(ctx.block());
		Code func = blk.join(asm.ret());
		functionBodies.put(ctx.Identifier().getText(), func);
		return Code.None; // don't hook onto other bodies
	}

	@Override
	public Code visitGlobalVarDeclaration(@NotNull WichParser.GlobalVarDeclarationContext ctx) {
		WichParser.VarDeclarationContext decl = ctx.varDeclaration();
		globalInitCode = globalInitCode.join(getVarInitCode(decl));
		return Code.None;
	}

	@Override
	public Code visitLocalVarDeclaration(@NotNull WichParser.LocalVarDeclarationContext ctx) {
		WichParser.VarDeclarationContext decl = ctx.varDeclaration();
		return getVarInitCode(decl);
	}

	public Code getVarInitCode(WichParser.VarDeclarationContext decl) {
		if ( decl.expression()!=null ) { // if initialized, generate a store.
			Code code = visit(decl);
			VariableSymbol symbol = (VariableSymbol)symtab.idDefToSymbol.get(decl.Identifier());
			Instr store;
			if ( symbol.scope==symtab.GLOBALS ) {
				store = asm.store_global(symbol.getSymbolIndex());
			}
			else {
				store = asm.store(symbol.getSymbolIndex());
			}
			code = code.join(store);
			return code;
		}
		return Code.None;
	}

	@Override
	public Code visitIfStatement(@NotNull WichParser.IfStatementContext ctx) {
		Code cond = visit(ctx.parExpression().expression());
		Code s1 = visit(ctx.statement(0)); // note that we are getting code out of order here (before gen of brf)
		if ( ctx.getChildCount()==3 ) { // if-then
			return CodeBlock.join(cond, asm.brf(s1.sizeBytes()+asm.br().size), s1);
		}
		else { // if-then-else
			Code s2 = visit(ctx.statement(1));
			return CodeBlock.join(cond,
								  asm.brf(s1.sizeBytes()+s2.sizeBytes()+asm.br().size + asm.br().size),
								  s1,
								  asm.br(s2.sizeBytes()+asm.br().size),
								  s2);
		}
	}

	@Override
	public Code visitForStatement(@NotNull WichParser.ForStatementContext ctx) {
		Code local = visit(ctx.localVarDeclaration()); // has code for init expr
		Code cond = visit(ctx.expression());
		Code iter = visit(ctx.assignmentStatement());
		Code blk = visit(ctx.statement());
		Code core = CodeBlock.join(
			cond,
			asm.brf(blk.sizeBytes() + iter.sizeBytes() + asm.br().size + asm.br().size), // jump over this one and final loopback BR
			blk,
			iter
		);
		return CodeBlock.join(
			local,
			core,
			asm.br(-core.sizeBytes()) // add BR to start of loop
		);
	}

	@Override
	public Code visitWhileStatement(@NotNull WichParser.WhileStatementContext ctx) {
		Code cond = visit(ctx.parExpression().expression());
		Code blk = visit(ctx.statement());
		Code all = CodeBlock.join(cond,
								  asm.brf(blk.sizeBytes() + asm.br().size + asm.br().size),
								  blk);
		return all.join(asm.br(-all.sizeBytes())); // add BR to start of loop
	}

	@Override
	public Code visitReturnStatement(@NotNull WichParser.ReturnStatementContext ctx) {
		if ( ctx.expression()!=null ) return visit(ctx.expression()).join(asm.retv());
		else return visit(ctx.expression()).join(asm.ret());
	}

	@Override
	public Code visitVarStore(@NotNull WichParser.VarStoreContext ctx) {
		Code valueCode = visit(ctx.expression());
		Instr store;
		VariableSymbol symbol = (VariableSymbol)symtab.idRefToSymbol.get(ctx.Identifier());
		if ( symbol.scope instanceof GlobalScope) {
			store = asm.store_global(symbol.getSymbolIndex());
		}
		else { // must be an arg or local
			store = asm.store(symbol.getSymbolIndex());
		}
		return valueCode.join(store);
	}

	@Override
	public Code visitFieldStore(@NotNull WichParser.FieldStoreContext ctx) {
		WichParser.ExpressionContext lvalue = ctx.expression(0);
		Code structValueCode = visit(lvalue);
		Code valueCode = visit(ctx.expression(1));
		StructSymbol s = (StructSymbol)lvalue.type;
		FieldSymbol field = (FieldSymbol)s.resolveMember(ctx.Identifier().getText());
		Instr store = asm.store_field(field.getSymbolIndex());
		return CodeBlock.join(structValueCode, valueCode, store);
	}

	@Override
	public Code visitIndexStore(@NotNull WichParser.IndexStoreContext ctx) {
		Code indexValueCode = visit(ctx.expression(0));
		Code valueCode = visit(ctx.expression(1));
		VariableSymbol symbol = (VariableSymbol)symtab.idRefToSymbol.get(ctx.Identifier());
		Code ld = load(symbol);
		Instr store = asm.store_index();
		return CodeBlock.join(ld, indexValueCode, valueCode, store);
	}

	@Override
	public Code visitCall(@NotNull WichParser.CallContext ctx) {
		Code args = visit(ctx.arguments());
		int i = symtab.functions.get(ctx.Identifier().getText()).functionIndex;
		return args.join(asm.call(i));
	}

	@Override
	public Code visitPrintStatement(@NotNull WichParser.PrintStatementContext ctx) {
		Code exprCode = visit(ctx.expression());
		Instr print;
		if ( ctx.expression().type == SymbolTable.int_type ) {
			print = asm.iprint();
		}
		else if ( ctx.expression().type == SymbolTable.float_type ) {
			print = asm.fprint();
		}
		else if ( ctx.expression().type == SymbolTable.char_type ) {
			print = asm.cprint();
		}
		else if ( ctx.expression().type == SymbolTable.boolean_type ) {
			print = asm.iprint();
		}
		else {
			print = asm.pprint();
		}
		return exprCode.join(print);
	}

	@Override
	public Code visitPrimary(@NotNull WichParser.PrimaryContext ctx) {
		Code code = visitChildren(ctx);
		Token firstToken = ctx.getStart();
		if ( firstToken.getType()==WichParser.Identifier ) {
			Symbol symbol = symtab.idRefToSymbol.get(ctx.Identifier());
			return code.join(load(symbol));
		}
		return code;
	}

	@Override
	public Code visitLiteral(@NotNull WichParser.LiteralContext ctx) {
		Token t = ctx.getStart();
		switch ( t.getType() ) {
			case WichParser.IntegerLiteral : return asm.iconst(Integer.valueOf(t.getText()));
			case WichParser.FloatingPointLiteral : return asm.fconst(Float.valueOf(t.getText()));
			case WichParser.CharacterLiteral :
				return asm.cconst(t.getText().charAt(1)); // TODO: handle escapes
			case WichParser.StringLiteral :
				int si = symtab.defineStringLiteral(t.getText());
				return asm.sconst(si);
			case WichParser.BooleanLiteral : return asm.iconst(t.getText().equals("true")?1:0);
			case WichParser.NULL : return asm.nil();
		}
		return Code.None;
	}

	@Override
	public Code visitFieldAccess(@NotNull WichParser.FieldAccessContext ctx) {
		Code structCode = visit(ctx.expression());
		Symbol symbol = symtab.idRefToSymbol.get(ctx.Identifier());
		if ( symbol!=null && symbol instanceof HasSymbolIndex ) {
			int i = ((HasSymbolIndex)symbol).getSymbolIndex();
			return structCode.join(asm.load_field(i));
		}
		return Code.None;
	}

	@Override
	public Code visitIndex(@NotNull WichParser.IndexContext ctx) {
		Code index = visit(ctx.expression());
		VariableSymbol symbol = (VariableSymbol)symtab.idRefToSymbol.get(ctx.Identifier());
		Code array = load(symbol);
		return CodeBlock.join(array, index, asm.load_index());
	}

	@Override
	public Code visitNewStruct(@NotNull WichParser.NewStructContext ctx) {
		StructSymbol symbol = (StructSymbol)symtab.idRefToSymbol.get(ctx.Identifier());
		return asm._new(symbol.getSymbolIndex());
	}

	@Override
	public Code visitNewStructArray(@NotNull WichParser.NewStructArrayContext ctx) {
		Code index = visit(ctx.expression());
		return index.join(asm.parray()); // TODO: which elem type?
	}

	@Override
	public Code visitNewPrimitiveArray(@NotNull WichParser.NewPrimitiveArrayContext ctx) {
		Code size = visit(ctx.expression());
		if ( ctx.arrayBuiltInType().type==SymbolTable.int_type ) {
			return size.join(asm.iarray());
		}
		else {
			return size.join(asm.farray());
		}
	}

	@Override
	public Code visitNegate(@NotNull WichParser.NegateContext ctx) {
		Code expr = visit(ctx.expression());
		Instr neg = ctx.type == SymbolTable.int_type ? asm.ineg() :  asm.fneg();
		return expr.join(neg);
	}

	@Override
	public Code visitNot(@NotNull WichParser.NotContext ctx) {
		Code expr = visit(ctx.expression());
		return expr.join(asm.not());
	}

	@Override
	public Code visitMult(@NotNull WichParser.MultContext ctx) {
		Code left = visit(ctx.expression(0));
		Code right = visit(ctx.expression(1));
		TerminalNode opNode = (TerminalNode)ctx.getChild(1);
		Code op = Code.None;
		switch ( opNode.getSymbol().getType() ) {
			case WichParser.MUL :
				op = ctx.type == SymbolTable.int_type ? asm.imul() : asm.fmul();
				break;
			case WichParser.DIV :
				op = ctx.type == SymbolTable.int_type ? asm.idiv() : asm.fdiv();
				break;
		}
		return CodeBlock.join(left, right, op);
	}

	@Override
	public Code visitAdd(@NotNull WichParser.AddContext ctx) {
		Code left = visit(ctx.expression(0));
		Code right = visit(ctx.expression(1));
		TerminalNode opNode = (TerminalNode)ctx.getChild(1);
		Code op = Code.None;
		switch ( opNode.getSymbol().getType() ) {
			case WichParser.ADD :
				op = ctx.type == SymbolTable.int_type ? asm.iadd() : asm.iadd();
				break;
			case WichParser.SUB :
				op = ctx.type == SymbolTable.int_type ? asm.isub() : asm.fsub();
				break;
		}
		return CodeBlock.join(left, right, op);
	}

	@Override
	public Code visitRelative(@NotNull WichParser.RelativeContext ctx) {
		Code left = visit(ctx.expression(0));
		Code right = visit(ctx.expression(1));
		TerminalNode opNode = (TerminalNode)ctx.getChild(1);
		Type type = ctx.expression(0).type;
		Code op = Code.None;
		switch ( opNode.getSymbol().getType() ) {
			case WichParser.LE : op = type==SymbolTable.int_type ? asm.ile() : asm.fle(); break;
			case WichParser.GE : op = type==SymbolTable.int_type ? asm.ige() : asm.fge(); break;
			case WichParser.GT : op = type==SymbolTable.int_type ? asm.igt() : asm.fgt(); break;
			case WichParser.LT : op = type==SymbolTable.int_type ? asm.ilt() : asm.flt(); break;
		}
		return CodeBlock.join(left, right, op);
	}

	@Override
	public Code visitEquality(@NotNull WichParser.EqualityContext ctx) {
		Code left = visit(ctx.expression(0));
		Code right = visit(ctx.expression(1));
		TerminalNode opNode = (TerminalNode)ctx.getChild(1);
		Type type = ctx.expression(0).type;
		Code op = Code.None;
		switch ( opNode.getSymbol().getType() ) {
			case WichParser.EQUAL    : op = type==SymbolTable.int_type ? asm.ieq()  : asm.feq(); break;
			case WichParser.NOTEQUAL : op = type==SymbolTable.int_type ? asm.ineq() : asm.fneq(); break;
		}
		return CodeBlock.join(left, right, op);
	}

	@Override
	public Code visitAnd(@NotNull WichParser.AndContext ctx) {
		Code left = visit(ctx.expression(0));
		Code right = visit(ctx.expression(1));
		return CodeBlock.join(left, right, asm.and());
	}

	@Override
	public Code visitOr(@NotNull WichParser.OrContext ctx) {
		Code left = visit(ctx.expression(0));
		Code right = visit(ctx.expression(1));
		return CodeBlock.join(left, right, asm.or());
	}

	public Code load(Symbol symbol) {
		if ( symbol!=null && symbol instanceof TypedSymbol) {
			TypedSymbol tsym = (TypedSymbol)symbol;
			if ( symbol.getScope() instanceof GlobalScope) {
				int i = symtab.globals.get(symbol.getName()).index;
				return asm.load_global(i);
			}
			else { // must be an arg or local
				HasSymbolIndex isym = (HasSymbolIndex)symbol;
				if ( tsym.getType() == SymbolTable.int_type ) {
					return asm.iload(isym.getSymbolIndex());
				}
				else if ( tsym.getType() == SymbolTable.float_type ) {
					return asm.fload(isym.getSymbolIndex());
				}
				else if ( tsym.getType() == SymbolTable.char_type ) {
					return asm.cload(isym.getSymbolIndex());
				}
				else if ( tsym.getType() == SymbolTable.boolean_type ) {
					return asm.iload(isym.getSymbolIndex());
				}
				else if ( tsym.getType() == SymbolTable.string_type ) {
					return asm.pload(isym.getSymbolIndex());
				}
				else {
					return asm.pload(isym.getSymbolIndex());
				}
			}
		}
		return Code.None;
	}

}
