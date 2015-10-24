package wich.semantics;

import org.antlr.symtab.Scope;
import org.antlr.symtab.Symbol;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import wich.errors.ErrorType;
import wich.errors.WichErrorHandler;
import wich.parser.WichBaseListener;
import wich.parser.WichParser;
import wich.semantics.symbols.WBuiltInTypeSymbol;
import wich.semantics.symbols.WFunctionSymbol;

import static wich.errors.ErrorType.INCORRECT_ARG_NUMBERS;


public class CommonWichListener extends WichBaseListener {
	protected final WichErrorHandler errorHandler;

	protected Scope currentScope;

	protected void pushScope(Scope s) {
		currentScope = s;
	}

	public CommonWichListener(WichErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	protected void popScope() {
		if (currentScope != null) {
			currentScope = currentScope.getEnclosingScope();
		}
	}

	public WBuiltInTypeSymbol resolveType(@NotNull String typeName) {
		Symbol typeSymbol = currentScope.resolve(typeName);
		if ( typeSymbol instanceof WBuiltInTypeSymbol ) {
			return (WBuiltInTypeSymbol)typeSymbol;
		}
		else {
			return null;
		}
	}

	void promoteArgTypes(WichParser.Call_exprContext ctx, WFunctionSymbol f) {
		int numOfArgs = f.argTypes.size();
		//check the number of args
		if(numOfArgs != 0 && numOfArgs != ctx.expr_list().expr().size()){
			error(ctx.start, INCORRECT_ARG_NUMBERS,
					String.valueOf(numOfArgs),
					String.valueOf(ctx.expr_list().expr().size()));
		}
		else {
			for (int i = 0; i < numOfArgs; i++) {
				WichParser.ExprContext argExpr = ctx.expr_list().expr(i);
				TypeHelper.promote(argExpr, f.argTypes.get(i));
			}
		}
	}

	// error support

	protected void error(Token token, ErrorType type, String... args) {
		errorHandler.error(token, type, args);
	}

	protected void error(Token token, ErrorType type, Exception e, String... args) {
		errorHandler.error(token, type, e, args);
	}

	public int getErrorNum() {
		return errorHandler.getErrorNum();
	}

	public String getErrorMessages() {
		return errorHandler.toString();
	}
}