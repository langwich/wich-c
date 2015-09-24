package wich.semantics;

import org.antlr.symtab.Scope;
import org.antlr.symtab.Symbol;
import org.antlr.symtab.Type;
import org.antlr.v4.runtime.misc.NotNull;
import wich.parser.WichBaseListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonWichListener extends WichBaseListener {
	public final List<String> errors = new ArrayList<>();
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
			error(typeName+" is not a type name");
			return null;
		}
	}

	// error support

	protected void error(String msg) {
		errors.add(msg);
	}

	protected void error(String msg, Exception e) {
		errors.add(msg + "\n" + Arrays.toString(e.getStackTrace()));
	}
}
