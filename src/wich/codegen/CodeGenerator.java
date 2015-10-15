package wich.codegen;

import org.antlr.symtab.Scope;
import org.antlr.symtab.Type;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import wich.codegen.model.ArgDef;
import wich.codegen.model.AssignStat;
import wich.codegen.model.Block;
import wich.codegen.model.BlockStatement;
import wich.codegen.model.BooleanType;
import wich.codegen.model.CallStat;
import wich.codegen.model.CompositeModelObject;
import wich.codegen.model.ElementAssignStat;
import wich.codegen.model.File;
import wich.codegen.model.FloatType;
import wich.codegen.model.Func;
import wich.codegen.model.FuncBlock;
import wich.codegen.model.IfStat;
import wich.codegen.model.IntType;
import wich.codegen.model.MainBlock;
import wich.codegen.model.MainFunc;
import wich.codegen.model.OutputModelObject;
import wich.codegen.model.PrintBooleanStat;
import wich.codegen.model.PrintFloatStat;
import wich.codegen.model.PrintIntStat;
import wich.codegen.model.PrintNewLine;
import wich.codegen.model.PrintStringStat;
import wich.codegen.model.PrintVectorStat;
import wich.codegen.model.ReturnStat;
import wich.codegen.model.Stat;
import wich.codegen.model.StringLiteral;
import wich.codegen.model.StringType;
import wich.codegen.model.StringVarDefStat;
import wich.codegen.model.VarDefStat;
import wich.codegen.model.VarInitStat;
import wich.codegen.model.VectorType;
import wich.codegen.model.VectorVarDefStat;
import wich.codegen.model.VoidType;
import wich.codegen.model.WhileStat;
import wich.codegen.model.WichType;
import wich.codegen.model.expr.BinaryOpExpr;
import wich.codegen.model.expr.BinaryPrimitiveOp;
import wich.codegen.model.expr.BinaryStringOp;
import wich.codegen.model.expr.BinaryVectorOp;
import wich.codegen.model.expr.Expr;
import wich.codegen.model.expr.FalseLiteral;
import wich.codegen.model.expr.FloatLiteral;
import wich.codegen.model.expr.FuncCall;
import wich.codegen.model.expr.HeapVarRef;
import wich.codegen.model.expr.IntLiteral;
import wich.codegen.model.expr.NegateExpr;
import wich.codegen.model.expr.NotExpr;
import wich.codegen.model.expr.StringIndexExpr;
import wich.codegen.model.expr.VarRef;
import wich.codegen.model.expr.VectorIndexExpr;
import wich.codegen.model.expr.VectorLiteral;
import wich.codegen.model.expr.promotion.StringFromFloat;
import wich.codegen.model.expr.promotion.StringFromInt;
import wich.codegen.model.expr.promotion.StringFromVector;
import wich.codegen.model.expr.promotion.VectorFromFloat;
import wich.codegen.model.expr.promotion.VectorFromInt;
import wich.parser.WichBaseVisitor;
import wich.parser.WichParser;
import wich.semantics.SymbolTable;
import wich.semantics.symbols.WBuiltInTypeSymbol;
import wich.semantics.symbols.WFunctionSymbol;
import wich.semantics.symbols.WString;
import wich.semantics.symbols.WVariableSymbol;
import wich.semantics.symbols.WVector;

import java.util.ArrayList;
import java.util.List;

import static wich.parser.WichParser.FunctionContext;

public class CodeGenerator extends WichBaseVisitor<OutputModelObject> {
	protected int blockNumber = 0; // tracks block number within each method
	protected STGroup templates;
	protected final SymbolTable symtab;
	protected File currentFile;
	protected Scope currentScope;
	protected Block currentBlock;

	public CodeGenerator(SymbolTable symtab) {
		this.templates = new STGroupFile("wich.stg");
		this.symtab = symtab;
	}

	public File generate(ParserRuleContext tree) {
		File f = (File)visit(tree);
		return f;
	}

	// TODO: try to add aggregate value thing

	// V I S I T O R  M E T H O D S

	@Override
	public OutputModelObject visitScript(@NotNull WichParser.ScriptContext ctx) {
		pushScope(symtab.getGlobalScope());

		List<Func> funcs = new ArrayList<>();
		for (WichParser.FunctionContext f : ctx.function()) {
			funcs.add((Func)visit(f));
		}

		MainBlock body = new MainBlock();
		body.scope = currentScope;

		for (WichParser.StatementContext s : ctx.statement()) {
			body.add((Stat) visit(s));
		}

		final WFunctionSymbol mainSym = new WFunctionSymbol("main");
		MainFunc main = new MainFunc(mainSym, body);

		currentFile = new File(funcs,main);

		popScope();
		return currentFile;
	}

	@Override
	public OutputModelObject visitFunction(@NotNull WichParser.FunctionContext ctx) {
		pushScope(ctx.scope);
		blockNumber = 0;

		WichType returnType = getTypeModel(SymbolTable._void);
		if ( ctx.type()!=null ) {
			returnType = (WichType)visit(ctx.type());
		}

		FuncBlock body = (FuncBlock)visit(ctx.block());
		Func func = new Func(ctx.scope, returnType, body);

		if ( ctx.formal_args()!=null ) {
			for (WichParser.Formal_argContext arg : ctx.formal_args().formal_arg()) {
				ArgDef argDefModel = (ArgDef) visit(arg);
				func.args.add(argDefModel);
			}
		}

		popScope();
		return func;
	}

	@Override
	public OutputModelObject visitFormal_arg(@NotNull WichParser.Formal_argContext ctx) {
		String name = ctx.ID().getText();
		WichType argType = (WichType) visit(ctx.type());
		return new ArgDef(name, argType);
	}

	@Override
	public OutputModelObject visitIntTypeSpec(WichParser.IntTypeSpecContext ctx) {
		return new IntType();
	}

	@Override
	public OutputModelObject visitBooleanTypeSpec(@NotNull WichParser.BooleanTypeSpecContext ctx) {
		return new BooleanType();
	}

	@Override
	public OutputModelObject visitFloatTypeSpec(WichParser.FloatTypeSpecContext ctx) {
		return new FloatType();
	}

	@Override
	public OutputModelObject visitStringTypeSpec(WichParser.StringTypeSpecContext ctx) {
		return new StringType();
	}

	@Override
	public OutputModelObject visitVectorTypeSpec(WichParser.VectorTypeSpecContext ctx) {
		return new VectorType();
	}

	@Override
	public OutputModelObject visitBlock(@NotNull WichParser.BlockContext ctx) {
		pushScope(ctx.scope);

		if ( ctx.getParent() instanceof FunctionContext ) {
			currentBlock = new FuncBlock();
		}
		else {
			currentBlock = new Block(currentBlock); // push new block
		}
		currentBlock.scope = currentScope;

		for (WichParser.StatementContext s : ctx.statement()) {
			Stat stat = (Stat)visit(s);
			currentBlock.add(stat);
		}

		Block result = currentBlock;
		currentBlock = currentBlock.enclosingBlock;
		popScope();
		return result;
	}

	// S T A T E M E N T S

	@Override
	public OutputModelObject visitBlockStatement(@NotNull WichParser.BlockStatementContext ctx) {
		return new BlockStatement( (Block)visit(ctx.block()) );
	}

	@Override
	public OutputModelObject visitIf(@NotNull WichParser.IfContext ctx) {
		IfStat ifStat    = new IfStat();
		ifStat.condition = (Expr)visit(ctx.expr());
		ifStat.stat      = (Stat)visit(ctx.statement(0));
		if (ctx.statement().size()>1) {
			ifStat.elseStat = (Stat)visit(ctx.statement(1));
		}
		return ifStat;
	}

	@Override
	public OutputModelObject visitWhile(@NotNull WichParser.WhileContext ctx) {
		WhileStat whileStat = new WhileStat();
		whileStat.condition = (Expr)visit(ctx.expr());
		whileStat.stat      = (Stat)visit(ctx.statement());
		return whileStat;
	}

	@Override
	public CompositeModelObject visitVardef(@NotNull WichParser.VardefContext ctx) {
		String varName = ctx.ID().getText();
		WVariableSymbol v = (WVariableSymbol)currentScope.resolve(varName);
		Expr expr = (Expr)visit(ctx.expr());
		VarInitStat varInit = new VarInitStat(getVarRef(varName), expr);
		VarDefStat varDef = getVarDefStat(v);
		return new CompositeModelObject(varDef, varInit);
	}

	@Override
	public OutputModelObject visitReturn(@NotNull WichParser.ReturnContext ctx) {
		final Expr exprModel = (Expr)visit(ctx.expr());
		ReturnStat ret = new ReturnStat(exprModel);
		ret.enclosingScope = currentScope;
		return ret;
	}

	@Override
	public OutputModelObject visitAssign(@NotNull WichParser.AssignContext ctx) {
		String varName = ctx.ID().getText();
		Expr expr      = (Expr)visit(ctx.expr());
		return new AssignStat(getVarRef(varName), expr);
	}

	@Override
	public OutputModelObject visitElementAssign(@NotNull WichParser.ElementAssignContext ctx) {
		String varName = ctx.ID().getText();
		Expr index     = (Expr)visit(ctx.expr(0));
		Expr expr      = (Expr)visit(ctx.expr(1));
		return new ElementAssignStat(getVarRef(varName), index, expr);
	}

	@Override
	public OutputModelObject visitCallStatement(@NotNull WichParser.CallStatementContext ctx) {
		CallStat callStat = new CallStat();
		callStat.callExpr = (Expr)visit(ctx.call_expr());
		return callStat;
	}

	@Override
	public OutputModelObject visitPrint(@NotNull WichParser.PrintContext ctx) {
		if ( ctx.expr()==null ) {
			return new PrintNewLine();
		}
		Expr expr = (Expr)visit(ctx.expr());
		return getPrintModel(ctx.expr().exprType, expr);
	}


	// E X P R E S S I O N S

	@Override
	public OutputModelObject visitOp(@NotNull WichParser.OpContext ctx) {
		Expr left  = (Expr)visit(ctx.expr(0));
		Expr right = (Expr)visit(ctx.expr(1));
		if (ctx.promoteToType != null) {
			left = createPromotionObject(ctx,left,right);
			right = createPromotionObject(ctx,right,left);
		}
		final Type resultType = ctx.promoteToType!=null ? ctx.promoteToType : ctx.exprType;
		return getBinaryOperationModel(ctx.operator(), resultType, left, right);
	}

	@Override
	public OutputModelObject visitNegate(@NotNull WichParser.NegateContext ctx) {
		return new NegateExpr((Expr)visit(ctx.expr()));
	}

	@Override
	public OutputModelObject visitNot(@NotNull WichParser.NotContext ctx) {
		return new NotExpr((Expr)visit(ctx.expr()));
	}

	@Override
	public OutputModelObject visitCall(@NotNull WichParser.CallContext ctx) {
		return visit(ctx.call_expr());
	}

	@Override
	public OutputModelObject visitCall_expr(@NotNull WichParser.Call_exprContext ctx) {
		String funcName = ctx.ID().getText();
		WFunctionSymbol funcSymbol = (WFunctionSymbol)currentScope.resolve(funcName);
		WichType retType = getTypeModel(funcSymbol.getType());
		FuncCall fc = new FuncCall(funcName, retType);
		if( ctx.expr_list()!=null ) {
			for (WichParser.ExprContext e : ctx.expr_list().expr()) {
				fc.args.add( (Expr)visit(e) );
			}
		}
		return fc;
	}

	@Override
	public OutputModelObject visitIndex(@NotNull WichParser.IndexContext ctx) {
		String varName = ctx.ID().getText();
		Expr index = (Expr)visit(ctx.expr());
		WVariableSymbol s = (WVariableSymbol)currentScope.resolve(varName);
		if ( s.getType()==SymbolTable._vector ) {
			return new VectorIndexExpr(varName, index);
		}
		return new StringIndexExpr(varName, index);
	}

	@Override
	public OutputModelObject visitParens(@NotNull WichParser.ParensContext ctx) {
		return visit(ctx.expr());
	}

	@Override
	public OutputModelObject visitAtom(@NotNull WichParser.AtomContext ctx) {
		return visit(ctx.primary());
	}

	@Override
	public OutputModelObject visitString(@NotNull WichParser.StringContext ctx) {
		StringLiteral s = new StringLiteral(ctx.getText());
		return s;
	}

	@Override
	public OutputModelObject visitVector(@NotNull WichParser.VectorContext ctx) {
		int length = ctx.expr_list().expr().size();
		VectorLiteral v = new VectorLiteral(length);
		for (WichParser.ExprContext e : ctx.expr_list().expr()) {
			v.elems.add((Expr)visit(e));
		}
		return v;
	}

	@Override
	public OutputModelObject visitInteger(@NotNull WichParser.IntegerContext ctx) {
		return new IntLiteral(ctx.getText());
	}

	@Override
	public OutputModelObject visitFloat(@NotNull WichParser.FloatContext ctx) {
		return new FloatLiteral(ctx.getText());
	}

	@Override
	public OutputModelObject visitFalseLiteral(@NotNull WichParser.FalseLiteralContext ctx) {
		return new FalseLiteral(ctx.getText());
	}

	@Override
	public OutputModelObject visitTrueLiteral(@NotNull WichParser.TrueLiteralContext ctx) {
		return new FalseLiteral(ctx.getText());
	}

	@Override
	public OutputModelObject visitIdentifier(@NotNull WichParser.IdentifierContext ctx) {
		final String varName = ctx.getText();
		return getVarRef(varName);
	}

	public VarRef getVarRef(String varName) {
		final WVariableSymbol varSym = (WVariableSymbol)currentScope.resolve(varName);
		return getVarRef(varSym);
	}

	public static VarRef getVarRef(WVariableSymbol varSym) {
		if ( isHeapType(varSym.getType()) ) {
			return new HeapVarRef(varSym);
		}
		return new VarRef(varSym);
	}

	public static VarDefStat getVarDefStat(WVariableSymbol varSym) {
		if ( varSym.getType() == SymbolTable._vector ) {
			return new VectorVarDefStat(varSym);
		}
		if ( varSym.getType() == SymbolTable._string ) {
			return new StringVarDefStat(varSym);
		}
		return new VarDefStat(varSym, getTypeModel(varSym.getType()));
	}


	// S U P P O R T  C O D E

	public static BinaryOpExpr getBinaryOperationModel(WichParser.OperatorContext opCtx,
													   Type operandType,
													   Expr left,
													   Expr right)
	{
		Token opToken = opCtx.getStart();
		String wichOp = opToken.getText();
		BinaryOpExpr opExpr;
		// split into granularity sufficient for most potential target languages
		if ( operandType == SymbolTable._vector ) {
			opExpr = new BinaryVectorOp(left, wichOp, right);
		}
		else if ( operandType == SymbolTable._string ) {
			opExpr = new BinaryStringOp(left, wichOp, right);
		}
		else {
			opExpr = new BinaryPrimitiveOp(left, wichOp, right);
		}
		opExpr.resultType = operandType;
		return opExpr;
	}

	public static Expr createPromotionObject( WichParser.OpContext ctx, Expr promoteExp,Expr targetExp) {
		if (promoteExp.getType() != ctx.promoteToType) {
			if (ctx.promoteToType == SymbolTable._vector) {
				promoteExp = promoteToVector(promoteExp, targetExp);
			}
			else if (ctx.promoteToType == SymbolTable._string) {
				promoteExp = promoteToString(promoteExp);
			}
		}
		return promoteExp;
	}

	private static Expr promoteToString(Expr promoteExp) {
		if (promoteExp.getType() == SymbolTable._vector) {
			StringFromVector s = new StringFromVector();
			s.vector = promoteExp;
			promoteExp = s;
		}
		else if (promoteExp.getType() == SymbolTable._int) {
			StringFromInt s = new StringFromInt();
			s.intExpr = promoteExp;
			promoteExp = s;
		}
		else if (promoteExp.getType() == SymbolTable._float) {
			StringFromFloat s = new StringFromFloat();
			s.floatExpr = promoteExp;
			promoteExp = s;
		}
		return promoteExp;
	}

	private static Expr promoteToVector(Expr promoteExp, Expr targetExp) {
		if (promoteExp.getType() == SymbolTable._int) {
			VectorFromInt v = new VectorFromInt();
			v.intLiteral = promoteExp;
			v.vector = targetExp;
			promoteExp = v;
		}
		else if (promoteExp.getType() == SymbolTable._float) {
			VectorFromFloat v = new VectorFromFloat();
			v.floatLiteral = promoteExp;
			v.vector = targetExp;
			promoteExp = v;
		}
		return promoteExp;
	}


	public static Stat getPrintModel(Type type, Expr expr) {
		// split into granularity sufficient for most potential target languages
		switch ( ((WBuiltInTypeSymbol)type).typename ) {
			case VECTOR :
				return new PrintVectorStat(expr);
			case STRING :
				return new PrintStringStat(expr);
			case INT :
				return new PrintIntStat(expr);
			case FLOAT:
				return new PrintFloatStat(expr);
			case BOOLEAN:
				return new PrintBooleanStat(expr);
		}
		return null;
	}

	public static boolean isHeapType(Type type) {
		return type instanceof WString || type instanceof WVector;
	}

	public static boolean isPrimitiveType(Type type) {
		return type instanceof WString || type instanceof WVector;
	}

	public static WichType getTypeModel(Type type) {
		if ( type instanceof WBuiltInTypeSymbol ) {
			switch ( ((WBuiltInTypeSymbol)type).typename ) {
				case VECTOR :
					return new VectorType();
				case STRING :
					return new StringType();
				case INT :
					return new IntType();
				case FLOAT:
					return new FloatType();
				case VOID:
					return new VoidType();
				case BOOLEAN:
					return new BooleanType();
				default :
					return null;
			}
		}
		return null;
	}

	protected void pushScope(Scope s) {currentScope = s;}

	protected void popScope() {currentScope = currentScope.getEnclosingScope();}
}
