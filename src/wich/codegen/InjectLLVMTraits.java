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

import wich.codegen.model.BlockInitialization;
import wich.codegen.model.BlockTermination;
import wich.codegen.model.BlockTerminationVoid;
import wich.codegen.model.ElementAssignStat;
import wich.codegen.model.Func;
import wich.codegen.model.MainFunc;
import wich.codegen.model.OutputModelObject;
import wich.codegen.model.expr.BinaryFloatOp;
import wich.codegen.model.expr.BinaryIntOp;
import wich.codegen.model.expr.BinaryPrimitiveOp;
import wich.codegen.model.expr.FloatLiteral;
import wich.codegen.model.expr.FuncCall;
import wich.codegen.model.expr.HeapVarRef;
import wich.codegen.model.expr.IntLiteral;
import wich.codegen.model.expr.VarRef;
import wich.codegen.model.expr.promotion.FloatFromInt;
import wich.semantics.SymbolTable;
import wich.semantics.symbols.WFunctionSymbol;

public class InjectLLVMTraits {

	protected WFunctionSymbol currentFunction;

	public OutputModelObject visitEveryModelObject(OutputModelObject o) {
		return o;
	}

	public OutputModelObject exitModel(Func func) {
//		System.out.println("exitModel func");
		if (func.returnType.type == SymbolTable._void) {
			func.body.terminate.add(new BlockTerminationVoid());
		}
		else {
			func.body.terminate.add(new BlockTermination(func.returnType));
			func.body.initialize.add(new BlockInitialization(func.returnType));
		}
		return func;
	}

	public OutputModelObject exitModel(MainFunc func) {
//		System.out.println("exitModel mainFunc");
		if (func.returnType.type == SymbolTable._void) {
			func.body.terminate.add(new BlockTerminationVoid());
		}
		else func.body.terminate.add(new BlockTermination(func.returnType));

		return func;
	}

	public OutputModelObject exitModel(BinaryPrimitiveOp op) {
		return getBinaryExprModel(op);
	}

	protected OutputModelObject getBinaryExprModel(BinaryPrimitiveOp op) {
		if (op.getType() == SymbolTable._float) {
			return new BinaryFloatOp(op);
		}
		else if (op.type.type == SymbolTable._int || op.type.type == SymbolTable._boolean) {
			return new BinaryIntOp(op);
		}

		else return op;
	}
}
