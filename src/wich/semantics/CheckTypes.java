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
import wich.semantics.symbols.WFunctionSymbol;

import static wich.errors.ErrorType.INCOMPATIBLE_ARGUMENT_ERROR;
import static wich.errors.ErrorType.INCOMPATIBLE_ASSIGNMENT_ERROR;
import static wich.errors.ErrorType.INVALID_CONDITION_ERROR;
import static wich.errors.ErrorType.INVALID_ELEMENT_ERROR;
import static wich.errors.ErrorType.INVALID_INDEX_ERROR;
import static wich.errors.ErrorType.INVALID_OPERATION;

public class CheckTypes extends MaintainScopeListener {

	public CheckTypes(WichErrorHandler errorHandler) {
		super(errorHandler);
	}

	@Override
	public void exitAssign(@NotNull WichParser.AssignContext ctx) {
		Symbol s = currentScope.resolve(ctx.ID().getText());
		Type left = ((TypedSymbol)s).getType();
		if ( !TypeHelper.isLegalAssign(left, ctx.expr()) ) {
			error(ctx.start, INCOMPATIBLE_ASSIGNMENT_ERROR, left.getName(), ctx.expr().exprType.getName());
		}
	}

	@Override
	public void exitElementAssign(@NotNull WichParser.ElementAssignContext ctx) {
		WichParser.ExprContext index = ctx.expr(0);
		WichParser.ExprContext elem = ctx.expr(1);
		//id must be of vector type
		Symbol id = currentScope.resolve(ctx.ID().getText());
		if (((TypedSymbol)id).getType() != SymbolTable._vector) {
			error(ctx.start, INVALID_OPERATION, "[]", ((TypedSymbol)id).getType().getName());
		}
		// index must be expression of int type
		else if (index.exprType != SymbolTable._int) {
			error(ctx.start, INVALID_INDEX_ERROR, index.exprType.getName()); //should terminate the program
		}
		// element value must be expression of float type or can be promoted to float
		else if ( !TypeHelper.typesAreCompatible(elem, SymbolTable._float) ) {
			error(ctx.start, INVALID_ELEMENT_ERROR, elem.exprType.getName());
		}
	}

	@Override
	public void exitVector(@NotNull WichParser.VectorContext ctx) {
		if (ctx.expr_list() != null) {
			for (WichParser.ExprContext elem : ctx.expr_list().expr()){
				if ( !TypeHelper.typesAreCompatible(elem, SymbolTable._float) ) {
					error(ctx.start, INVALID_ELEMENT_ERROR, elem.exprType.getName());
				}
			}
		}
	}

	@Override
	public void exitIf(@NotNull WichParser.IfContext ctx) {
		if(ctx.expr().exprType != SymbolTable._boolean)
			error(ctx.start, INVALID_CONDITION_ERROR, ctx.expr().exprType.getName());
	}


	@Override
	public void exitWhile(@NotNull WichParser.WhileContext ctx) {
		if(ctx.expr().exprType != SymbolTable._boolean)
			error(ctx.start, INVALID_CONDITION_ERROR, ctx.expr().exprType.getName());
	}

	@Override
	public void exitCall_expr(@NotNull WichParser.Call_exprContext ctx) {
		Symbol f = currentScope.resolve(ctx.ID().getText());
		if(f != null && f instanceof WFunctionSymbol){
			int numOfArgs = ((WFunctionSymbol)f).argTypes.size();
			if(numOfArgs != 0 && numOfArgs == ctx.expr_list().expr().size()){
				for(int i = 0; i < numOfArgs; i++){
					Type actual = ctx.expr_list().expr(i).exprType;
					Type promoted = ctx.expr_list().expr(i).promoteToType;
					Type expected = ((WFunctionSymbol)f).argTypes.get(i);
					if (actual != expected && promoted != expected)
						error(ctx.start, INCOMPATIBLE_ARGUMENT_ERROR, expected.getName(), actual.getName());
				}
			}
		}
	}

	@Override
	public void exitCall(@NotNull WichParser.CallContext ctx) {
		WichParser.Call_exprContext callExprContext = ctx.call_expr();
		exitCall_expr(callExprContext);
	}
}
