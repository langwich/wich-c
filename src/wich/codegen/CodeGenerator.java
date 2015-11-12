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
import wich.codegen.model.FuncCallVoid;
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
import wich.codegen.model.RefCountDEREF;
import wich.codegen.model.RefCountDEREFVector;
import wich.codegen.model.RefCountREF;
import wich.codegen.model.RefCountREFVector;
import wich.codegen.model.ReturnHeapVarStat;
import wich.codegen.model.ReturnStat;
import wich.codegen.model.ReturnVectorHeapVarStat;
import wich.codegen.model.Stat;
import wich.codegen.model.StringDecl;
import wich.codegen.model.expr.StringLiteral;
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
import wich.codegen.model.expr.VectorElement;
import wich.codegen.model.expr.VectorIndexExpr;
import wich.codegen.model.expr.VectorLiteral;
import wich.codegen.model.expr.promotion.FloatFromInt;
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
	protected WFunctionSymbol currentFunction;

	protected List<StringDecl> strDecls = new ArrayList<>();

	protected static final String PROMO = "promo";

	public CodeGenerator(SymbolTable symtab) {
		this.templates = new STGroupFile("wich.stg");
		this.symtab = symtab;
	}

	public File generate(ParserRuleContext tree) {
		return (File)visit(tree);
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

		final WFunctionSymbol mainSym = new WFunctionSymbol("main");
		mainSym.setEnclosingScope(currentScope);

		enterFunction(mainSym);
		for (WichParser.StatementContext s : ctx.statement()) {
			body.add((Stat) visit(s));
		}
		exitFunction();
		MainFunc main = new MainFunc(mainSym, body);

		currentFile = new File(funcs,main, strDecls);

		popScope();
		return currentFile;
	}

	@Override
	public OutputModelObject visitFunction(@NotNull WichParser.FunctionContext ctx) {
		enterFunction(ctx.scope);
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

		exitFunction();
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
		IfStat ifStat    = new IfStat(getIfLabel());
		ifStat.condition = (Expr)visit(ctx.expr());
		ifStat.stat      = (Stat)visit(ctx.statement(0));
		if (ctx.statement().size()>1) {
			ifStat.elseStat = (Stat)visit(ctx.statement(1));
		}
		return ifStat;
	}

	@Override
	public OutputModelObject visitWhile(@NotNull WichParser.WhileContext ctx) {
		WhileStat whileStat = new WhileStat(getWhileLabel());
		whileStat.condition = (Expr)visit(ctx.expr());
		whileStat.stat      = (Stat)visit(ctx.statement());
		return whileStat;
	}

	@Override
	public CompositeModelObject visitVardef(@NotNull WichParser.VardefContext ctx) {
		String varName = ctx.ID().getText();
		WVariableSymbol v = (WVariableSymbol)currentScope.resolve(varName);
		Expr expr = (Expr)visit(ctx.expr());
		VarInitStat varInit = new VarInitStat(getVarRef(varName, true), expr, getTypeModel(expr.getType()));
		VarDefStat varDef = getVarDefStat(v);
		return new CompositeModelObject(varDef, varInit);
	}

	@Override
	public OutputModelObject visitReturn(@NotNull WichParser.ReturnContext ctx) {
		final Expr exprModel = (Expr)visit(ctx.expr());
		ReturnStat ret = new ReturnStat(exprModel, getReturnLabel());
		ret.enclosingScope = currentScope;
		ret.returnType = getTypeModel(exprModel.getType());
		return ret;
	}

	@Override
	public OutputModelObject visitAssign(@NotNull WichParser.AssignContext ctx) {
		String varName = ctx.ID().getText();
		Expr expr      = (Expr)visit(ctx.expr());
		return new AssignStat(getVarRef(varName, true), expr, getTypeModel(expr.getType()));
	}

	@Override
	public OutputModelObject visitElementAssign(@NotNull WichParser.ElementAssignContext ctx) {
		String varName = ctx.ID().getText();
		VarRef vecRef = getVarRef(varName, false);
		Expr index     = (Expr)visit(ctx.expr(0));
		Expr expr      = (Expr)visit(ctx.expr(1));
		return new ElementAssignStat(vecRef, index, expr);
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
			return new PrintNewLine(getPrintLabel());
		}
		Expr expr = (Expr)visit(ctx.expr());
		return getPrintModel(ctx.expr().exprType, expr, getPrintLabel());
	}


	// E X P R E S S I O N S

	@Override
	public OutputModelObject visitOp(@NotNull WichParser.OpContext ctx) {
		Expr left  = (Expr)visit(ctx.expr(0));
		Expr right = (Expr)visit(ctx.expr(1));
		if (ctx.promoteToType != null) {
			left = getPromotionObject(ctx, left, right);
			right = getPromotionObject(ctx, right, left);
		}
		final Type resultType = ctx.promoteToType!=null ? ctx.promoteToType : ctx.exprType;
		Expr model = getBinaryOperationModel(ctx.operator(), resultType, left, right, getTempVar());
		return model;
	}

	@Override
	public OutputModelObject visitNegate(@NotNull WichParser.NegateContext ctx) {
		return new NegateExpr((Expr)visit(ctx.expr()), getTypeModel(ctx.exprType), getTempVar());
	}

	@Override
	public OutputModelObject visitNot(@NotNull WichParser.NotContext ctx) {
		return new NotExpr((Expr)visit(ctx.expr()), getTempVar());
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
		if (funcSymbol.getType() == SymbolTable._void)
			fc = new FuncCallVoid(funcName, retType);

		if( ctx.expr_list()!=null ) {
			for (WichParser.ExprContext e : ctx.expr_list().expr()) {
				Expr arg = (Expr) visit(e);
				fc.args.add( arg );
			}
		}

		if (funcSymbol.getType() != SymbolTable._void) fc.varRef = getTempVar();

		return fc;
	}

	@Override
	public OutputModelObject visitIndex(@NotNull WichParser.IndexContext ctx) {
		String varName = ctx.ID().getText();
		Expr index = (Expr)visit(ctx.expr());
		WVariableSymbol s = (WVariableSymbol)currentScope.resolve(varName);
		if ( s.getType()==SymbolTable._vector ) {
			return new VectorIndexExpr(varName, index, getTempVar());
		}
		return new StringIndexExpr(varName, index, getTempVar());
	}

	@Override
	public OutputModelObject visitParens(@NotNull WichParser.ParensContext ctx) {
		return visit(ctx.expr());
	}

	@Override
	public OutputModelObject visitAtom(@NotNull WichParser.AtomContext ctx) {
		Expr primary = (Expr) visit(ctx.primary());
		return getPromotionObject(ctx, primary);
	}

	@Override
	public OutputModelObject visitString(@NotNull WichParser.StringContext ctx) {
		String tempVarRef = getTempVar();
		StringLiteral sl = new StringLiteral(ctx.getText(), tempVarRef);
		String declStr = getDeclString(sl.literal);
		strDecls.add(new StringDecl(declStr, declStr.length()-2, tempVarRef));
		return sl;
	}

	@Override
	public OutputModelObject visitVector(@NotNull WichParser.VectorContext ctx) {
		int length = ctx.expr_list().expr().size();
		String vecRef = getTempVar();
		VectorLiteral v = new VectorLiteral(length, vecRef);
		for (int i = 0; i < ctx.expr_list().expr().size(); ++i) {
			v.elems.add(new VectorElement((Expr) visit(ctx.expr_list().expr(i)), i, vecRef));
		}
		v.varRef = getTempVar();
		return v;
	}

	@Override
	public OutputModelObject visitInteger(@NotNull WichParser.IntegerContext ctx) {
		return new IntLiteral(ctx.getText(), getTempVar());
	}

	@Override
	public OutputModelObject visitFloat(@NotNull WichParser.FloatContext ctx) {
		return new FloatLiteral(ctx.getText(), getTempVar());
	}

	@Override
	public OutputModelObject visitFalseLiteral(@NotNull WichParser.FalseLiteralContext ctx) {
		return new FalseLiteral(ctx.getText(), getTempVar());
	}

	@Override
	public OutputModelObject visitTrueLiteral(@NotNull WichParser.TrueLiteralContext ctx) {
		return new FalseLiteral(ctx.getText(), getTempVar());
	}

	@Override
	public OutputModelObject visitIdentifier(@NotNull WichParser.IdentifierContext ctx) {
		final String varName = ctx.getText();
		return getVarRef(varName, false);
	}

	// S U P P O R T  C O D E

	public VarRef getVarRef(String varName, boolean isAssign) {
		final WVariableSymbol varSym = (WVariableSymbol)currentScope.resolve(varName);
		return getVarRef(varSym, isAssign ? "" : getTempVar());
	}

	public static VarRef getVarRef(WVariableSymbol varSym) {
		VarRef varRef = new VarRef(varSym, getTypeModel(varSym.getType()));
		if ( isHeapType(varSym.getType()) ) {
			return new HeapVarRef(varSym, getTypeModel(varSym.getType()));
		}
		return varRef;
	}

	public static VarRef getVarRef(WVariableSymbol varSym, String tempVarRef) {
		VarRef varRef = new VarRef(varSym, getTypeModel(varSym.getType()));
		if ( isHeapType(varSym.getType()) ) {
			varRef = new HeapVarRef(varSym, getTypeModel(varSym.getType()));
		}
		varRef.varRef = tempVarRef;
		return varRef;
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

	public static RefCountREF getREF(WVariableSymbol varSym) {
		final VarRef varRef = getVarRef(varSym);
		if (varSym.getType() == SymbolTable._vector) {
			return new RefCountREFVector(varRef);
		}
		else {
			return new RefCountREF(varRef);
		}
	}

	public static RefCountDEREF getDEREF(WVariableSymbol varSym) {
		final VarRef varRef = getVarRef(varSym);
		if (varSym.getType() == SymbolTable._vector) {
			return new RefCountDEREFVector(varRef);
		}
		else {
			return new RefCountDEREF(varRef);
		}
	}

	public static ReturnHeapVarStat getReturnHeapExpr(Expr expr) {
		if (expr.getType() == SymbolTable._vector) {
			return new ReturnVectorHeapVarStat(expr);
		}
		else {
			return new ReturnHeapVarStat(expr);
		}
	}

	public static BinaryOpExpr getBinaryOperationModel(WichParser.OperatorContext opCtx,
													   Type operandType,
													   Expr left,
													   Expr right,
	                                                   String tempVarRef)
	{
		Token opToken = opCtx.getStart();
		String wichOp = opToken.getText();
		BinaryOpExpr opExpr;
		// split into granularity sufficient for most potential target languages
		if ( operandType == SymbolTable._vector ) {
			opExpr = new BinaryVectorOp(left, wichOp, right, tempVarRef);
		}
		else if ( operandType == SymbolTable._string ) {
			opExpr = new BinaryStringOp(left, wichOp, right, tempVarRef);
		}
		else {
			opExpr = new BinaryPrimitiveOp(left, wichOp, right, getTypeModel(left.getType()), tempVarRef);
		}
		opExpr.resultType = operandType;
		return opExpr;
	}

	public Expr getPromotionObject(WichParser.AtomContext ctx, Expr promoteExp) {
		if (promoteExp.getType() != ctx.promoteToType) {
			if (ctx.promoteToType == SymbolTable._float) {
				promoteExp = promoteToFloat(promoteExp, getPromoteVarRef());
			}
		}
		return promoteExp;
	}

	public Expr getPromotionObject(WichParser.OpContext ctx, Expr promoteExp, Expr targetExp) {
		if (promoteExp.getType() != ctx.promoteToType) {
			if (ctx.promoteToType == SymbolTable._vector) {
				promoteExp = promoteToVector(promoteExp, targetExp, getPromoteVarRef());
			}
			else if (ctx.promoteToType == SymbolTable._string) {
				promoteExp = promoteToString(promoteExp, getPromoteVarRef());
			}
			else if (ctx.promoteToType == SymbolTable._float) {
				promoteExp = promoteToFloat(promoteExp, getPromoteVarRef());
			}
		}
		return promoteExp;
	}

	private static Expr promoteToFloat(Expr promoteExp, String tempVar) {
		if (promoteExp.getType() == SymbolTable._int) {
			promoteExp = new FloatFromInt(promoteExp, tempVar);
		}
		return promoteExp;
	}

	private static Expr promoteToString(Expr promoteExp, String tempVar) {
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
		promoteExp.varRef = tempVar;
		return promoteExp;
	}

	private static Expr promoteToVector(Expr promoteExp, Expr targetExp, String tempVar) {
		if (promoteExp.getType() == SymbolTable._int) {
			promoteExp = new VectorFromInt(promoteExp, targetExp);
		}
		else if (promoteExp.getType() == SymbolTable._float) {
			promoteExp = new VectorFromFloat(promoteExp, targetExp);
		}
		promoteExp.varRef = tempVar;
		return promoteExp;
	}


	public static Stat getPrintModel(Type type, Expr expr, String num) {
		// split into granularity sufficient for most potential target languages
		switch ( ((WBuiltInTypeSymbol)type).typename ) {
			case VECTOR :
				return new PrintVectorStat(expr, num);
			case STRING :
				return new PrintStringStat(expr, num);
			case INT :
				return new PrintIntStat(expr, num);
			case FLOAT:
				return new PrintFloatStat(expr, num);
			case BOOLEAN:
				return new PrintBooleanStat(expr, num);
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

	protected static String getDeclString(String strWithQuotes) {
		return strWithQuotes.substring(1, strWithQuotes.length()-1)+"\\00";
	}

	protected void pushScope(Scope s) {currentScope = s;}

	protected void popScope() {currentScope = currentScope.getEnclosingScope();}

	protected void enterFunction(Scope s) {
		pushScope(s);
		currentFunction = (WFunctionSymbol) s;
	}

	protected void exitFunction() {
		popScope();
		currentFunction = null;
	}

	protected String getTempVar() {
		return currentFunction == null ? "" : String.valueOf(currentFunction.getTempVar());
	}

	protected String getIfLabel() {
		return currentFunction == null ? "" : String.valueOf(currentFunction.getNextIfNum());
	}

	protected String getWhileLabel() {
		return currentFunction == null ? "" : String.valueOf(currentFunction.getNextWhileNum());
	}

	protected String getReturnLabel() {
		return currentFunction == null ? "" : String.valueOf(currentFunction.getNextReturnNum());
	}

	protected String getPrintLabel() {
		return currentFunction == null ? "" : String.valueOf(currentFunction.getNextPrintNum());
	}

	protected String getPromoteVarRef() {
		return currentFunction == null ? "" : PROMO+currentFunction.getNextPromoteNum();
	}
}
