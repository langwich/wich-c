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
import org.antlr.v4.runtime.misc.NotNull;
import wich.errors.WichErrorHandler;
import wich.parser.WichParser;
import wich.semantics.symbols.WBuiltInTypeSymbol;

import static wich.errors.WichErrorHandler.INCOMPATIBLE_ASSIGNMENT;
import static wich.errors.WichErrorHandler.INVALID_ELEMENT;
import static wich.errors.WichErrorHandler.INVALID_VECTOR_INDEX;

public class TypeChecker extends MaintainScopeListener {
	public TypeChecker(WichErrorHandler errorHandler) {
		super(errorHandler);
	}

	@Override
	public void exitAssign(@NotNull WichParser.AssignContext ctx) {
		Symbol s = currentScope.resolve(ctx.ID().getText());
		Type left = (WBuiltInTypeSymbol) s;
		Type right = ctx.expr().exprType;

		if ( !TypeHelper.isLegalAssign(left, right) ) {
			error(INCOMPATIBLE_ASSIGNMENT, left.getName()+"="+right.getName());
		}
	}

	@Override
	public void exitElementAssign(@NotNull WichParser.ElementAssignContext ctx) {
		WichParser.ExprContext index = ctx.expr(0);
		WichParser.ExprContext elem = ctx.expr(1);

		// index must be expression of int type
		if (index.exprType != SymbolTable._int) {
			error(INVALID_VECTOR_INDEX, index.exprType.getName());
		}

		// element value must be expression of float type or can be promoted to float
		if ( !TypeHelper.typesAreCompatible(elem, SymbolTable._float) ) {
			error(INVALID_ELEMENT, elem.exprType.getName());
		}
	}

	@Override
	public void exitVector(@NotNull WichParser.VectorContext ctx) {
		if (ctx.expr_list() != null) {
			for (WichParser.ExprContext elem : ctx.expr_list().expr()){
				if ( !TypeHelper.typesAreCompatible(elem, SymbolTable._float) ) {
					error(INVALID_ELEMENT, elem.exprType.getName());
				}
			}
		}
	}
}
