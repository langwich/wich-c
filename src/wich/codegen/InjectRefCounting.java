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
package wich.codegen;

import org.antlr.symtab.Scope;
import wich.codegen.model.ArgDef;
import wich.codegen.model.AssignStat;
import wich.codegen.model.Block;
import wich.codegen.model.CompositeModelObject;
import wich.codegen.model.Func;
import wich.codegen.model.FuncBlock;
import wich.codegen.model.MainBlock;
import wich.codegen.model.OutputModelObject;
import wich.codegen.model.RefCountREF;
import wich.codegen.model.ReturnStat;
import wich.codegen.model.VarInitStat;
import wich.semantics.symbols.WVariableSymbol;

public class InjectRefCounting {
	protected Func currentFunc;
	protected Scope currentScope;

	public OutputModelObject visitEveryModelObject(OutputModelObject o) {
//		System.out.println("visit every node: "+o.getClass().getSimpleName());
		return o;
	}

	public OutputModelObject exitModel(VarInitStat init) {
		return exitModel((AssignStat)init);
	}

	public OutputModelObject exitModel(AssignStat assign) {
//		System.out.println("exitModel assignment");
		if ( CodeGenerator.isHeapType(assign.expr.getType()) ) {
			final String varName = assign.varRef.getName();
			final WVariableSymbol varSym = (WVariableSymbol)currentScope.resolve(varName);
			final RefCountREF REF = CodeGenerator.getREF(varSym);
			return new CompositeModelObject(assign, REF);
		}
		return assign;
	}

	public OutputModelObject exitModel(ReturnStat retStat) {
		if ( CodeGenerator.isHeapType(retStat.expr.getType()) ) {
			return CodeGenerator.getReturnHeapExpr(retStat.expr);
//			if ( retStat.expr instanceof VarRef ) { // TODO: maybe we always need to wrap?
//			}
		}
		return retStat;
	}

	public OutputModelObject enterModel(Func func) {
		currentFunc = func;
		return func;
	}

	public OutputModelObject exitModel(Func func) {
//		System.out.println("exitModel func");
		// Inject REF(x) for all heap args x at start of function, DEREF at end
		for (ArgDef arg : func.args) {
			if ( CodeGenerator.isHeapType(arg.type.type) ) {
				final WVariableSymbol argSym = (WVariableSymbol)func.scope.resolve(arg.name);
				func.body.stats.add(0, CodeGenerator.getREF(argSym));
			}
		}

//		func.body.cleanup.add(new RefCountDEREF());

		currentFunc = null;
		return func;
	}

	public OutputModelObject enterModel(MainBlock script) {
		enterModel((Block)script);
		return script;
	}

	public OutputModelObject exitModel(MainBlock script) {
		exitModel((Block) script);
		return script;
	}

	public OutputModelObject enterModel(FuncBlock block) {
		return enterModel((Block)block);
	}

	public OutputModelObject exitModel(FuncBlock block) {
		return exitModel((Block)block);
	}

	public OutputModelObject enterModel(Block block) {
//		System.out.println("enterModel block");
		pushScope(block.scope);
		return block;
	}

	public OutputModelObject exitModel(Block block) {
//		System.out.println("exitModel Block");
//		for (VarDefStat varDef : block.varDefs) {
//			if ( CodeGenerator.isHeapType(varDef.type.type) ) {
//			}
//		}
		popScope();
		return block;
	}

	protected void pushScope(Scope s) {currentScope = s;}

	protected void popScope() {currentScope = currentScope.getEnclosingScope();}
}
