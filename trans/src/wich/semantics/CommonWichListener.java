package wich.semantics;

import org.antlr.symtab.Scope;
import org.antlr.symtab.Symbol;
import org.antlr.symtab.Type;
import org.antlr.v4.runtime.misc.NotNull;
import wich.errors.WichErrorHandler;
import wich.parser.WichBaseListener;

import java.util.Arrays;

public class CommonWichListener extends WichBaseListener {
	protected final WichErrorHandler errorHandler = new WichErrorHandler();
	protected Scope currentScope;

	protected void pushScope(Scope s) {
		currentScope = s;
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
			error(WichErrorHandler.UNDEFINED_TYPE, "(" + typeName + ")");
			return null;
		}
	}

	// error support

	protected void error(int type, String msg) {
		errorHandler.aggregate(type, msg);
	}

	protected void error(int type, String msg, Exception e) {
		errorHandler.aggregate(type, msg + "\n" + Arrays.toString(e.getStackTrace()));
	}
}
