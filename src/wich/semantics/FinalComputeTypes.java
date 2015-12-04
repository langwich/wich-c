package wich.semantics;/*
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

/*Another pass to support forward reference */
public class FinalComputeTypes extends MaintainScopeListener {

	public FinalComputeTypes(WichErrorHandler errorHandler) {
		super(errorHandler);
	}

	@Override
	public void exitElementAssign(@NotNull WichParser.ElementAssignContext ctx) {
		// might need to promote right hand side per known type of left
		ExprContext expr = ctx.expr(1);
		Symbol id = currentScope.resolve(ctx.ID().getText());
		if ( id instanceof WVariableSymbol ) {
			TypeHelper.promote(expr, SymbolTable._float);
		}
	}

	@Override
	public void exitAssign(@NotNull WichParser.AssignContext ctx) {
		// might need to promote right hand side per known type of left
		ExprContext expr = ctx.expr();
		Symbol id = currentScope.resolve(ctx.ID().getText());
		if ( id instanceof WVariableSymbol ) {
			TypeHelper.promote(expr, ((WVariableSymbol) id).getType());
		}
	}

	@Override
	public void exitOp(@NotNull WichParser.OpContext ctx) {
		if (ctx.exprType != null) return;
		int op = ctx.operator().start.getType();
		ExprContext lExpr = ctx.expr(0);
		ExprContext rExpr = ctx.expr(1);
		//check if operand is valid
		if( lExpr.exprType == SymbolTable.INVALID_TYPE ||
			rExpr.exprType == SymbolTable.INVALID_TYPE )
		{
			if(lExpr.exprType == SymbolTable.INVALID_TYPE) {
				error(lExpr.start, INVALID_OPERAND_ERROR, lExpr.getText());
			}
			if(rExpr.exprType == SymbolTable.INVALID_TYPE) {
				error(rExpr.start, INVALID_OPERAND_ERROR, rExpr.getText());
			}
		}
		//when both operands' types are known
		else if(lExpr.exprType != null && rExpr.exprType != null) {
			ctx.exprType = SymbolTable.op(op, lExpr, rExpr);
			ctx.promoteToType = TypeHelper.getPromoteType(op, ctx);
			if(ctx.exprType == SymbolTable.INVALID_TYPE) {
				String left = lExpr.exprType.getName();
				String operator = ctx.operator().getText();
				String right = rExpr.exprType.getName();
				error(ctx.start, INCOMPATIBLE_OPERAND_ERROR, left, operator, right);
			}
		}
	}

	@Override
	public void exitNegate(@NotNull WichParser.NegateContext ctx) {
		if (ctx.exprType != null) return;
		ctx.exprType = ctx.expr().exprType;
	}

	@Override
	public void exitNot(@NotNull WichParser.NotContext ctx) {
		if (ctx.exprType != null) return;
		ctx.exprType = ctx.expr().exprType;
	}

	@Override
	public void exitCall_expr(@NotNull WichParser.Call_exprContext ctx) {
		if (ctx.exprType != null) return;
		Symbol f = currentScope.resolve(ctx.ID().getText());
		if ( f!=null && f instanceof WFunctionSymbol ) {
			ctx.exprType = ((WFunctionSymbol) f).getType();
			int numOfArgs = ((WFunctionSymbol) f).argTypes.size();
			if(numOfArgs != 0 && numOfArgs == ctx.expr_list().expr().size()) {
				promoteArgTypes(ctx, (WFunctionSymbol) f);
			}
		}
	}

	@Override
	public void exitCall(@NotNull WichParser.CallContext ctx) {
		if (ctx.exprType != null) return;
		Symbol f = currentScope.resolve(ctx.call_expr().ID().getText());
		if ( f!=null && f instanceof WFunctionSymbol ) {
			ctx.exprType = ((WFunctionSymbol) f).getType();
			int numOfArgs = ((WFunctionSymbol) f).argTypes.size();
			if(numOfArgs != 0 && numOfArgs == ctx.call_expr().expr_list().expr().size()) {
				WichParser.Call_exprContext callExprContext = ctx.call_expr();
				promoteArgTypes(callExprContext, (WFunctionSymbol) f);
			}
		}
	}

	@Override
	public void exitIndex(@NotNull WichParser.IndexContext ctx) {
		if (ctx.exprType != null) return;
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
		} else if (idType != null){
			error(ctx.ID().getSymbol(), INVALID_OPERATION, "[]", ((WVariableSymbol) s).getType().getName());
		}
	}

	@Override
	public void exitParens(@NotNull WichParser.ParensContext ctx) {
		if (ctx.exprType != null) return;
		ctx.exprType = ctx.expr().exprType;
	}

	@Override
	public void exitIdentifier(@NotNull WichParser.IdentifierContext ctx) {
		if (ctx.exprType != null) return;
		Symbol s = currentScope.resolve(ctx.ID().getText());
		if ( s!=null && s instanceof WVariableSymbol ) {
			ctx.exprType = ((TypedSymbol) s).getType();
		} else {
			error(ctx.ID().getSymbol(), SYMBOL_NOT_FOUND, ctx.ID().getText());
		}
	}

	@Override
	public void exitInteger(@NotNull WichParser.IntegerContext ctx) {
		if (ctx.exprType != null) return;
		ctx.exprType = SymbolTable._int;
	}

	@Override
	public void exitFloat(@NotNull WichParser.FloatContext ctx) {
		if (ctx.exprType != null) return;
		ctx.exprType = SymbolTable._float;
	}

	@Override
	public void exitVector(@NotNull WichParser.VectorContext ctx) {
		ctx.exprType = SymbolTable._vector;
		// promote element type to fit in a vector
		for (ExprContext elem : ctx.expr_list().expr()) {
			if(elem.exprType != null) { // may not be known at this stage
				TypeHelper.promote(elem, SymbolTable._float);
			}
		}
	}

	@Override
	public void exitString(@NotNull WichParser.StringContext ctx) {
		if (ctx.exprType != null) return;
		ctx.exprType = SymbolTable._string;
	}

	@Override
	public void exitAtom(@NotNull WichParser.AtomContext ctx) {
		if (ctx.exprType != null) return;
		ctx.exprType = ctx.primary().exprType; // bubble up primary's type to expr node
	}

	@Override
	public void exitLen(WichParser.LenContext ctx) {
		if (ctx.exprType != null) return;
		ctx.exprType = SymbolTable._int;
	}
}
