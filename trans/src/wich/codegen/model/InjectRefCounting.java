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
package wich.codegen.model;

import org.antlr.symtab.Symbol;
import wich.codegen.CodeGenerator;
import wich.semantics.symbols.WVariableSymbol;

import java.util.ArrayList;
import java.util.List;

public class InjectRefCounting {
//	public OutputModelObject visitEveryModelObject(OutputModelObject o) {
//		System.out.println("visit every node: "+o.getClass().getSimpleName());
//		return o;
//	}

	public OutputModelObject enterModel(AssignStat assign) {
		System.out.println("enterModel assignment");
		return assign;
	}

	public OutputModelObject exitModel(VarInitStat assign) {
		System.out.println("exitModel assignment for var init");
		return assign;
	}

	public OutputModelObject exitModel(Func func) {
		System.out.println("exitModel func");
		// Inject REF(x) for all heap args x at start of function, DEREF at end
		for (ArgDef arg : func.args) {
			if ( CodeGenerator.isHeapType(arg.type.type) ) {
				func.body.stats.add(0, new RefCountREF(arg.name));
				func.body.stats.add(new RefCountDEREF(arg.name));
			}
		}

		return func;
	}

	public OutputModelObject exitModel(Script script) {
		return exitModel((Block) script);
	}

	public OutputModelObject exitModel(Block block) {
		System.out.println("exitModel Block");
		for (VarDefStat varDef : block.varDefs) {
			if ( CodeGenerator.isHeapType(varDef.type.type) ) {
				block.add(new RefCountDEREF(varDef.name));
			}
		}
		return block;
	}

	public OutputModelObject exitModel(ReturnStat retStat) {
		System.out.println("exitModel return stat");
		// add DEREF for all heap vars
		final List<Stat> DEREFs = new ArrayList<>();
		for (Symbol sym : retStat.enclosingScope.getSymbols()) {
			if ( sym instanceof WVariableSymbol) {
				if ( CodeGenerator.isHeapType(((WVariableSymbol) sym).getType()) ) {
					DEREFs.add(new RefCountDEREF(sym.getName()));
				}
			}
		}
		final CompositeModelObject retWithDEREFs = new CompositeModelObject();
		retWithDEREFs.addAll(DEREFs);
		retWithDEREFs.add(retStat);

		return retWithDEREFs;
	}
}
