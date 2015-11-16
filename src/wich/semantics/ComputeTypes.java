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


import org.antlr.symtab.Symbol;
import org.antlr.symtab.Type;
import org.antlr.symtab.TypedSymbol;
import org.antlr.v4.runtime.misc.NotNull;
import wich.errors.WichErrorHandler;
import wich.parser.WichParser;
import wich.parser.WichParser.ExprContext;
import wich.semantics.symbols.WFunctionSymbol;
import wich.semantics.symbols.WVariableSymbol;

import static wich.errors.ErrorType.INCOMPATIBLE_OPERAND_ERROR;
import static wich.errors.ErrorType.INVALID_OPERAND_ERROR;
import static wich.errors.ErrorType.INVALID_OPERATION;
import static wich.errors.ErrorType.SYMBOL_NOT_FOUND;
import static wich.errors.ErrorType.UNDEFINED_FUNCTION;

/*Compute expression types wherever possible.*/
public class ComputeTypes extends MaintainScopeListener {

	public ComputeTypes(WichErrorHandler errorHandler) {
		super(errorHandler);
	}

	@Override
	public void exitOp(@NotNull WichParser.OpContext ctx) {
		int op = ctx.operator().start.getType();
		ExprContext lExpr = ctx.expr(0);
		ExprContext rExpr = ctx.expr(1);
		//check if operand is valid
		if (lExpr.exprType == SymbolTable.INVALID_TYPE ||
			rExpr.exprType == SymbolTable.INVALID_TYPE)
		{
			if (lExpr.exprType == SymbolTable.INVALID_TYPE) {
				error(lExpr.start, INVALID_OPERAND_ERROR, lExpr.getText());
			}
			if (rExpr.exprType == SymbolTable.INVALID_TYPE) {
				error(rExpr.start, INVALID_OPERAND_ERROR, rExpr.getText());
			}
		}
		else if (lExpr.exprType != null && rExpr.exprType != null) {
			ctx.exprType = SymbolTable.op(op, lExpr, rExpr);
			ctx.promoteToType = lExpr.promoteToType == null ? rExpr.promoteToType : lExpr.promoteToType;
			if (ctx.exprType == SymbolTable.INVALID_TYPE) {
				String left = lExpr.exprType.getName();
				String operator = ctx.operator().getText();
				String right = rExpr.exprType.getName();
				error(ctx.start, INCOMPATIBLE_OPERAND_ERROR, left, operator, right);
			}
		}
	}

	@Override
	public void exitNegate(@NotNull WichParser.NegateContext ctx) {
		ctx.exprType = ctx.expr().exprType;
	}

	@Override
	public void exitNot(@NotNull WichParser.NotContext ctx) {
		//! expr, expr is a boolean
		ctx.exprType = ctx.expr().exprType;
	}

	@Override
	public void exitCall_expr(@NotNull WichParser.Call_exprContext ctx) {
		Symbol f = currentScope.resolve(ctx.ID().getText());
		if ( f!=null && f instanceof WFunctionSymbol ) {
			ctx.exprType = ((WFunctionSymbol) f).getType();
			promoteArgTypes(ctx, (WFunctionSymbol) f);
		} else {
			error(ctx.ID().getSymbol(), UNDEFINED_FUNCTION, ctx.ID().getText());
		}
	}

	@Override
	public void exitCall(@NotNull WichParser.CallContext ctx) {
		Symbol f = currentScope.resolve(ctx.call_expr().ID().getText());
		if ( f!=null && f instanceof WFunctionSymbol ) {
			ctx.exprType = ((WFunctionSymbol) f).getType();
			WichParser.Call_exprContext callExprContext = ctx.call_expr();
			promoteArgTypes(callExprContext, (WFunctionSymbol)f);
		} else {
			error(ctx.call_expr().ID().getSymbol(), UNDEFINED_FUNCTION, ctx.call_expr().ID().getText());
		}
	}

	@Override
	public void exitIndex(@NotNull WichParser.IndexContext ctx) {
		Symbol s = currentScope.resolve(ctx.ID().getText());
		if ( s==null) {
			error(ctx.ID().getSymbol(), SYMBOL_NOT_FOUND, ctx.ID().getText());
			return;
		}
		// string[i] returns a single character string
		Type idType = ((WVariableSymbol) s).getType();
		if ( idType==SymbolTable._string ) {        //string can be indexed
			ctx.exprType = SymbolTable._string;
		} else if ( idType==SymbolTable._vector ) {         // vector can be indexed
			ctx.exprType = SymbolTable._float;
		} else if (idType != null) {
			error(ctx.start, INVALID_OPERATION, "[]", ((WVariableSymbol) s).getType().getName());
		}
	}

	@Override
	public void exitParens(@NotNull WichParser.ParensContext ctx) {
		ctx.exprType = ctx.expr().exprType;
	}

	@Override
	public void exitIdentifier(@NotNull WichParser.IdentifierContext ctx) {
		Symbol s = currentScope.resolve(ctx.ID().getText());
		if ( s!=null && s instanceof WVariableSymbol ) {
			ctx.exprType = ((TypedSymbol) s).getType();
		} else {
			error(ctx.ID().getSymbol(), SYMBOL_NOT_FOUND, ctx.ID().getText());
		}
	}

	@Override
	public void exitInteger(@NotNull WichParser.IntegerContext ctx) {
		ctx.exprType = SymbolTable._int;
	}

	@Override
	public void exitFloat(@NotNull WichParser.FloatContext ctx) {
		ctx.exprType = SymbolTable._float;
	}

	@Override
	public void exitVector(@NotNull WichParser.VectorContext ctx) {
		ctx.exprType = SymbolTable._vector;
		// promote element type to fit in a vector
		for (ExprContext elem : ctx.expr_list().expr()) {
			if (elem.exprType != null) { // may not be known at this stage
				TypeHelper.promote(elem, SymbolTable._float);
			}
		}
	}

	@Override
	public void exitString(@NotNull WichParser.StringContext ctx) {
		ctx.exprType = SymbolTable._string;
	}

	@Override
	public void exitTrueLiteral(@NotNull WichParser.TrueLiteralContext ctx) {
		ctx.exprType = SymbolTable._boolean;
	}

	@Override
	public void exitFalseLiteral(@NotNull WichParser.FalseLiteralContext ctx) {
		ctx.exprType = SymbolTable._boolean;
	}

	@Override
	public void exitAtom(@NotNull WichParser.AtomContext ctx) {
		ctx.exprType = ctx.primary().exprType; // bubble up primary's type to expr node
	}
}
