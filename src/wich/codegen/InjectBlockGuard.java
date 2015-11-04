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
import wich.codegen.model.Func;
import wich.codegen.model.MainFunc;
import wich.codegen.model.OutputModelObject;
import wich.codegen.model.BlockTermination;
import wich.codegen.model.BlockTerminationVoid;
import wich.semantics.SymbolTable;

public class InjectBlockGuard {
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
}
