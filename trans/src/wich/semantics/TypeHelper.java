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
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import wich.parser.WichParser;
import wich.parser.WichParser.ExprContext;
import wich.semantics.symbols.WBuiltInTypeSymbol;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static wich.parser.WichParser.ADD;
import static wich.parser.WichParser.AND;
import static wich.parser.WichParser.DIV;
import static wich.parser.WichParser.EQUAL_EQUAL;
import static wich.parser.WichParser.GE;
import static wich.parser.WichParser.GT;
import static wich.parser.WichParser.LE;
import static wich.parser.WichParser.LT;
import static wich.parser.WichParser.MUL;
import static wich.parser.WichParser.NOT_EQUAL;
import static wich.parser.WichParser.OR;
import static wich.parser.WichParser.SUB;
import static wich.semantics.SymbolTable._boolean;
import static wich.semantics.SymbolTable._float;
import static wich.semantics.SymbolTable._int;
import static wich.semantics.SymbolTable._string;
import static wich.semantics.SymbolTable._vector;

public class TypeHelper {
	protected static final WBuiltInTypeSymbol[][][] opResultTypeMap =
		new WBuiltInTypeSymbol[WichParser.tokenNames.length+1][][];
	protected static final WBuiltInTypeSymbol[][][] operandPromotionMap =
		new WBuiltInTypeSymbol[WichParser.tokenNames.length+1][][];

	// ---------------------------- Result Type Table ----------------------------
	// *, / -
	protected static final WBuiltInTypeSymbol[][] arithmeticResultTable = new WBuiltInTypeSymbol[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {_int,      _float,     null,       _vector,    null},
	/*float*/	{_float,    _float,     null,       _vector,    null},
	/*string*/	{null,      null,       null,       null,       null},
	/*vector*/	{_vector,   _vector,    null,       _vector,    null},
	/*boolean*/	{null,      null,       null,       null,       null}
	};
	// +
	protected static final WBuiltInTypeSymbol[][] arithmeticStrResultTable = new WBuiltInTypeSymbol[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {_int,      _float,     _string,    _vector,    null},
	/*float*/	{_float,    _float,     _string,    _vector,    null},
	/*string*/	{_string,   _string,    _string,    _string,    null},
	/*vector*/	{_vector,   _vector,    _string,    _vector,    null},
	/*boolean*/	{null,      null,       null,       null,       null}
	};
	// <, <=, >, >=
	protected static final WBuiltInTypeSymbol[][] relationalResultTable = new WBuiltInTypeSymbol[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {_boolean,  _boolean,   null,       null,       null},
	/*float*/	{_boolean,  _boolean,   null,       null,       null},
	/*string*/	{null,      null,       _boolean,   null,       null},
	/*vector*/	{null,      null,       null,       null,       null},
	/*boolean*/	{null,      null,       null,       null,       null}
	};
	// ==, !=
	protected static final WBuiltInTypeSymbol[][] equalityResultTable = new WBuiltInTypeSymbol[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {_boolean,  _boolean,   null,       null,       null},
	/*float*/	{_boolean,  _boolean,   null,       null,       null},
	/*string*/	{null,      null,       _boolean,   null,       null},
	/*vector*/	{null,      null,       null,       _boolean,   null},
	/*boolean*/	{null,      null,       null,       null,       _boolean}
	};
	// and, or
	protected static final WBuiltInTypeSymbol[][] logicalResultTable = new WBuiltInTypeSymbol[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {null,      null,       null,       null,       null},
	/*float*/	{null,      null,       null,       null,       null},
	/*string*/	{null,      null,       null,       null,       null},
	/*vector*/	{null,      null,       null,       null,       null},
	/*boolean*/	{null,      null,       null,       null,       _boolean}
	};

	// ---------------------------- Type Promotion Table ----------------------------
	// *, /, -
	protected static final WBuiltInTypeSymbol[][] arithmeticPromoteFromTo = new WBuiltInTypeSymbol[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {null,      _float,     null,       _vector,    null},
	/*float*/	{null,      null,       null,       _vector,    null},
	/*string*/	{null,      null,       null,       null,       null},
	/*vector*/	{null,      null,       null,       null,       null},
	/*boolean*/	{null,      null,       null,       null,       null}
	};
	// +
	protected static final WBuiltInTypeSymbol[][] arithmeticStrPromoteFromTo = new WBuiltInTypeSymbol[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {null,      _float,     _string,    _vector,    null},
	/*float*/	{null,      null,       _string,    _vector,    null},
	/*string*/	{null,      null,       null,       null,       null},
	/*vector*/	{null,      null,       _string,    null,       null},
	/*boolean*/	{null,      null,       null,       null,       null}
	};
	// <, <=, >, >=
	protected static final WBuiltInTypeSymbol[][] relationalPromoteFromTo = new WBuiltInTypeSymbol[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {null,      _float,     null,       null,       null},
	/*float*/	{null,      null,       null,       null,       null},
	/*string*/	{null,      null,       null,       null,       null},
	/*vector*/	{null,      null,       null,       null,       null},
	/*boolean*/	{null,      null,       null,       null,       null}
	};
	// ==, !=
	protected static final WBuiltInTypeSymbol[][] equalityPromoteFromTo = new WBuiltInTypeSymbol[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {null,      _float,     null,       null,       null},
	/*float*/	{null,      null,       null,       null,       null},
	/*string*/	{null,      null,       null,       null,       null},
	/*vector*/	{null,      null,       null,       null,       null},
	/*boolean*/	{null,      null,       null,       null,       null}
	};
	// and, or
	protected static final WBuiltInTypeSymbol[][] logicalPromoteFromTo = new WBuiltInTypeSymbol[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {null,      null,       null,       null,       null},
	/*float*/	{null,      null,       null,       null,       null},
	/*string*/	{null,      null,       null,       null,       null},
	/*vector*/	{null,      null,       null,       null,       null},
	/*boolean*/	{null,      null,       null,       null,       null}
	};

	static {
		// register result tables.
		opResultTypeMap[MUL] = arithmeticResultTable;
		opResultTypeMap[SUB] = arithmeticResultTable;
		opResultTypeMap[DIV] = arithmeticResultTable;
		opResultTypeMap[ADD] = arithmeticStrResultTable;

		opResultTypeMap[LT]  = relationalResultTable;
		opResultTypeMap[LE]  = relationalResultTable;
		opResultTypeMap[GT]  = relationalResultTable;
		opResultTypeMap[GE]  = relationalResultTable;

		opResultTypeMap[EQUAL_EQUAL] = equalityResultTable;
		opResultTypeMap[NOT_EQUAL]   = equalityResultTable;

		opResultTypeMap[AND] = logicalResultTable;
		opResultTypeMap[OR]  = logicalResultTable;
		// register promote tables.
		operandPromotionMap[MUL] = arithmeticPromoteFromTo;
		operandPromotionMap[SUB] = arithmeticPromoteFromTo;
		operandPromotionMap[DIV] = arithmeticPromoteFromTo;
		operandPromotionMap[ADD] = arithmeticStrPromoteFromTo;

		operandPromotionMap[LT]  = relationalPromoteFromTo;
		operandPromotionMap[LE]  = relationalPromoteFromTo;
		operandPromotionMap[GT]  = relationalPromoteFromTo;
		operandPromotionMap[GE]  = relationalPromoteFromTo;

		operandPromotionMap[EQUAL_EQUAL] = equalityPromoteFromTo;
		operandPromotionMap[NOT_EQUAL]   = equalityPromoteFromTo;

		operandPromotionMap[AND] = logicalPromoteFromTo;
		operandPromotionMap[OR]  = logicalPromoteFromTo;
	}

	/** This method is the general helper method used to calculate result type.
	 *  You should use the method in SymbolTable based on this method.
	 */
	public static WBuiltInTypeSymbol getResultType(int op,
												   ExprContext le,
												   ExprContext re)
	{
		int li = ((WBuiltInTypeSymbol)le.exprType).getTypeIndex();
		int ri = ((WBuiltInTypeSymbol)re.exprType).getTypeIndex();
		WBuiltInTypeSymbol resultType = opResultTypeMap[op][li][ri];
		le.promoteToType = operandPromotionMap[op][li][resultType.getTypeIndex()];
		re.promoteToType = operandPromotionMap[op][ri][resultType.getTypeIndex()];
		return resultType;
	}

	/** This method is used to promote type in assignment.
	 *  Returns a boolean indicating whether the assignment is legal.
	 */
	public static boolean isLegalAssign(Type ltype,
	                                    Type rtype)
	{
		if (ltype == rtype) {
			return true;
		}
		else {
			int li = ((WBuiltInTypeSymbol)ltype).getTypeIndex();
			int ri = ((WBuiltInTypeSymbol)rtype).getTypeIndex();
			return equalityPromoteFromTo[li][ri].getTypeIndex() == li;
		}
	}

	/** This method is used to promote type during type annotation */
	public static void promote(ExprContext elem, int targetIndex) {
		int selfIndex = ((WBuiltInTypeSymbol)elem.exprType).getTypeIndex();
		elem.promoteToType = equalityPromoteFromTo[selfIndex][targetIndex];
	}

	public static String dumpWithType(ParserRuleContext tree) {
		if (tree == null) return "";
		return String.valueOf(process(tree.children, (t)->t instanceof TerminalNode, (t)->"",
						(t)->dumpNonTerminal((ParserRuleContext) t)));
	}

	private static String dumpNonTerminal(ParserRuleContext ctx) {
		StringBuilder sb = new StringBuilder();
		if (ctx instanceof ExprContext) {
			sb.append(dumpExprWithType(ctx));
		}
		else {
			sb.append(dumpWithType(ctx));
		}
		return sb.toString();
	}

	protected static String dumpExprWithType(ParserRuleContext ctx) {
		StringBuilder sb = new StringBuilder();
		if (ctx instanceof WichParser.AtomContext) {
			sb.append(dumpPrimaryWithType((WichParser.AtomContext) ctx));
		}
		else if (ctx instanceof WichParser.OpContext) {
			sb.append(dumpOpWithType((WichParser.OpContext) ctx));
		}
		else if (ctx instanceof WichParser.CallContext) {
			sb.append(dumpCallWithType((WichParser.CallContext) ctx));
		}
		else {
			ExprContext exprCtx = (ExprContext) ctx;
			sb.append(exprCtx.getText()).append(":").append(getPrintType(exprCtx)).append("\n");
			sb.append(process(exprCtx.children, (t)->t instanceof ExprContext,
			                  (t)->dumpExprWithType((ParserRuleContext) t),
			                  (t)->""));
		}
		return sb.toString();
	}

	protected static String dumpOpWithType(WichParser.OpContext ctx) {
		return String.valueOf(process(ctx.children,
				(t)->t instanceof ExprContext,
				(t)->dumpExprWithType((ParserRuleContext) t),
				(t)->"")) + ctx.operator().getText() + ":" + getPrintType(ctx) + "\n";
	}

	protected static String dumpCallWithType(WichParser.CallContext ctx) {
		WichParser.Expr_listContext args = ctx.call_expr().expr_list();
		return ctx.getText() + ":" + getPrintType(ctx) + "\n" +
				String.valueOf(process(args!=null ? args.children : Collections.emptyList(),
				                       (t) -> t instanceof ExprContext,
				                       (t) -> dumpExprWithType((ParserRuleContext) t),
				                       (t) -> ""));
	}

	protected static String dumpPrimaryWithType(WichParser.AtomContext ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append(ctx.getText()).append(":").append(getPrintType(ctx)).append("\n");
		if (ctx.primary() instanceof WichParser.VectorContext) {
			WichParser.VectorContext vectorContext = (WichParser.VectorContext) ctx.primary();
			sb.append(process(vectorContext.expr_list().expr(), (t)->true, TypeHelper::dumpExprWithType, (t)->""));
		}
		return sb.toString();
	}

	protected static String getPrintType(ExprContext ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append(ctx.exprType.getName());
		if (ctx.promoteToType != null) {
			sb.append(" => ").append(ctx.promoteToType);
		}
		return sb.toString();
	}

	protected static <T, R> StringBuilder process(List<T> children,
	                                              Predicate<T> pred,
	                                              Function<T, R> func1,
	                                              Function<T, R> func2) {
		StringBuilder sb = new StringBuilder();
		for (T child : children) {
			if (pred.test(child)) {
				sb.append(func1.apply(child));
			}
			else {
				sb.append(func2.apply(child));
			}
		}
		return sb;
	}

	public static boolean typesAreCompatible(ExprContext elem, Type type) {
		return elem.exprType == type || elem.promoteToType == type;
	}
}
