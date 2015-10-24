package wich.semantics;

import org.antlr.symtab.Scope;
import org.antlr.symtab.Symbol;
import org.antlr.symtab.Type;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import wich.errors.ErrorType;
import wich.errors.WichErrorHandler;
import wich.parser.WichBaseListener;


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

	public Type resolveType(@NotNull String typeName) {
		Symbol typeSymbol = currentScope.resolve(typeName);
		if ( typeSymbol instanceof Type ) {
			return (Type)typeSymbol;
		}
		else {
			return null;
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