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

import wich.codegen.model.ArgDef;
import wich.codegen.model.BlockInitialization;
import wich.codegen.model.BlockTermination;
import wich.codegen.model.BlockTerminationVoid;
import wich.codegen.model.Func;
import wich.codegen.model.MainFunc;
import wich.codegen.model.OutputModelObject;
import wich.codegen.model.ScopedArgDef;
import wich.codegen.model.StringVarDefStat;
import wich.codegen.model.VarDefStat;
import wich.codegen.model.VectorVarDefStat;
import wich.codegen.model.expr.BinaryFloatOp;
import wich.codegen.model.expr.BinaryIntOp;
import wich.codegen.model.expr.BinaryPrimitiveOp;
import wich.codegen.model.expr.HeapVarRef;
import wich.codegen.model.expr.NegateExpr;
import wich.codegen.model.expr.NegateFloatExpr;
import wich.codegen.model.expr.NegateIntExpr;
import wich.codegen.model.expr.ScopedStringIndexExpr;
import wich.codegen.model.expr.ScopedVarDefStat;
import wich.codegen.model.expr.ScopedVarRef;
import wich.codegen.model.expr.ScopedVectorIndexExpr;
import wich.codegen.model.expr.StringIndexExpr;
import wich.codegen.model.expr.VarRef;
import wich.codegen.model.expr.VectorIndexExpr;
import wich.semantics.SymbolTable;
import wich.semantics.symbols.WFunctionSymbol;

public class InjectLLVMTraits {

	protected WFunctionSymbol currentFunction;

	public OutputModelObject visitEveryModelObject(OutputModelObject o) {
		return o;
	}

	public OutputModelObject exitModel(Func func) {
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
		if (func.returnType.type == SymbolTable._void) {
			func.body.terminate.add(new BlockTerminationVoid());
		}
		else func.body.terminate.add(new BlockTermination(func.returnType));

		return func;
	}

	public OutputModelObject exitModel(VarDefStat varDefStat) {
		return new ScopedVarDefStat(varDefStat.symbol, varDefStat.type);
	}

	public OutputModelObject exitModel(StringVarDefStat varDefStat) {
		return new ScopedVarDefStat(varDefStat.symbol, varDefStat.type);
	}

	public OutputModelObject exitModel(VectorVarDefStat varDefStat) {
		return new ScopedVarDefStat(varDefStat.symbol, varDefStat.type);
	}

	public OutputModelObject exitModel(VarRef varRef) {
		return new ScopedVarRef(varRef.symbol, varRef.type, varRef.varRef);
	}

	public OutputModelObject exitModel(StringIndexExpr e) {
		return new ScopedStringIndexExpr(e.varName, e.symbol, e.expr, e.varRef);
	}

	public OutputModelObject exitModel(VectorIndexExpr e) {
		return new ScopedVectorIndexExpr(e.varName, e.symbol, e.expr, e.varRef);
	}

	public OutputModelObject exitModel(HeapVarRef heapVarRef) {
		return new ScopedVarRef(heapVarRef.symbol, heapVarRef.type, heapVarRef.varRef);
	}

	public OutputModelObject exitModel(ArgDef argDef) {
		return new ScopedArgDef(argDef.symbol, argDef.type);
	}

	public OutputModelObject exitModel(BinaryPrimitiveOp op) {
		return getBinaryExprModel(op);
	}

	public OutputModelObject exitModel(NegateExpr n) {
		return getNegateExprModel(n);
	}

	protected OutputModelObject getNegateExprModel(NegateExpr n) {
		n = new NegateIntExpr(n.expr, n.type, n.varRef);
		if (n.getType() == SymbolTable._float) {
			n = new NegateFloatExpr(n.expr, n.type, n.varRef);
		}
		return n;
	}

	protected OutputModelObject getBinaryExprModel(BinaryPrimitiveOp op) {
		if (op.type.type == SymbolTable._float) {
			return new BinaryFloatOp(op);
		}
		else if (op.type.type == SymbolTable._int || op.type.type == SymbolTable._boolean) {
			return new BinaryIntOp(op);
		}
		else return op;
	}
}
