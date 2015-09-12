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

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import wich.parser.WichParser;
import wich.parser.WichParser.ExprContext;
import wich.semantics.type.WBuiltInTypeSymbol;

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

	// This method is the general helper method used to calculate result type.
	// You should use the method in SymbolTable based on this method.
	public static WBuiltInTypeSymbol getResultType(int op,
												   ExprContext le,
												   ExprContext re)
	{
		int li = le.exprType.getTypeIndex();
		int ri = re.exprType.getTypeIndex();
		WBuiltInTypeSymbol resultType = opResultTypeMap[op][li][ri];
		le.promoteToType = operandPromotionMap[op][li][resultType.getTypeIndex()];
		re.promoteToType = operandPromotionMap[op][ri][resultType.getTypeIndex()];
		return resultType;
	}

	public static String dumpWithType(ParserRuleContext tree) {
		if (tree == null) return "";
		StringBuilder sb = new StringBuilder();
		for (ParseTree child : tree.children) {
			if (child instanceof TerminalNode) continue;
			ParserRuleContext ctx = (ParserRuleContext) child;
			if (ctx instanceof ExprContext) {
				sb.append(dumpExprWithType(ctx));
			}
			else {
				sb.append(dumpWithType((ParserRuleContext) child));
			}
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
		else {
			ExprContext exprCtx = (ExprContext) ctx;
			for (ParseTree parseTree : exprCtx.children) {
				sb.append(dumpWithType((ParserRuleContext) parseTree));
			}
		}
		return sb.toString();
	}

	protected static String dumpOpWithType(WichParser.OpContext ctx) {
		StringBuilder sb = new StringBuilder();
		for (ParseTree parseTree : ctx.children) {
			if (parseTree instanceof ExprContext)
				sb.append(dumpExprWithType((ParserRuleContext) parseTree));
		}
		sb.append(ctx.operator().getText()).append(":");
		sb.append(getPrintType(ctx)).append("\n");
		return sb.toString();
	}

	protected static String dumpPrimaryWithType(WichParser.AtomContext ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append(ctx.getText()).append(":").append(getPrintType(ctx)).append("\n");
		WichParser.PrimaryContext primary = ctx.primary();
		if (primary.getChildCount() > 1) {
			for (ExprContext exprContext : primary.expr_list().expr()) {
				sb.append(dumpExprWithType(exprContext));
			}
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
}
