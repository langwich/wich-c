package wich.semantics;

import org.antlr.v4.runtime.misc.NotNull;
import wich.errors.WichErrorHandler;
import wich.parser.WichParser;

public class MaintainScopeListener extends CommonWichListener {
	public MaintainScopeListener(WichErrorHandler errorHandler) {
		super(errorHandler);
	}

	@Override
	public void enterScript(@NotNull WichParser.ScriptContext ctx) {
		pushScope(ctx.scope);
	}

	@Override
	public void exitScript(@NotNull WichParser.ScriptContext ctx) {
		popScope();
	}

	@Override
	public void enterFunction(@NotNull WichParser.FunctionContext ctx) {
		pushScope(ctx.scope);
	}

	@Override
	public void exitFunction(@NotNull WichParser.FunctionContext ctx) {
		popScope();
	}

	@Override
	public void enterBlock(@NotNull WichParser.BlockContext ctx) {
		pushScope(ctx.scope);
	}

	@Override
	public void exitBlock(@NotNull WichParser.BlockContext ctx) {
		popScope();
	}
}
