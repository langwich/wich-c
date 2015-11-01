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
package wich.semantics.symbols;

import org.antlr.symtab.FunctionSymbol;
import org.antlr.symtab.Type;
import wich.semantics.SymbolTable;

import java.util.ArrayList;

public class WFunctionSymbol extends FunctionSymbol {
	public WBlock block; // code block of the function
	public int address;
	public ArrayList<WBuiltInTypeSymbol> argTypes = new ArrayList<>();

	public WFunctionSymbol(String funcName) {
		super(funcName);
		address = -1;
	}

	public WBuiltInTypeSymbol getType() {
		return (WBuiltInTypeSymbol)retType;
	}

	public int nargs() {
		int count = 0 ;
		for(org.antlr.symtab.Symbol v :getSymbols()){
			if (v instanceof WArgSymbol){
				count++;
			}
		}
		return count;
	}

	public int nlocals() {
		int num = getSymbols().size()-nargs();
		if (block != null) {
			num += block.getSymbols().size();
		}
		return num;
	}


}
