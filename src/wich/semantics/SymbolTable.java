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
package wich.semantics;

import org.antlr.symtab.BaseScope;
import org.antlr.symtab.GlobalScope;
import org.antlr.symtab.InvalidType;
import org.antlr.symtab.PredefinedScope;
import org.antlr.symtab.Scope;
import org.antlr.symtab.Symbol;
import org.antlr.symtab.Type;
import wich.parser.WichParser;
import wich.parser.WichParser.ExprContext;
import wich.semantics.symbols.*;

import java.util.*;

public class SymbolTable {
	public BaseScope PREDEFINED = new PredefinedScope();
	public GlobalScope GLOBALS = new GlobalScope(PREDEFINED);
	public static final Type INVALID_TYPE = new InvalidType();

	public HashMap<String,Integer> strings = new HashMap<>();
	private int strIndex = -1;

	public static final WInt _int = new WInt();
	public static final WFloat _float = new WFloat();
	public static final WString _string = new WString();
	public static final WVector _vector = new WVector();
	public static final WBoolean _boolean = new WBoolean();
	public static final WVoid _void = new WVoid();

	public int numOfVars;

	public SymbolTable() {
		initTypeSystem();
	}

	protected void initTypeSystem() {
		PREDEFINED.define(_int);
		PREDEFINED.define(_float);
		PREDEFINED.define(_string);
		PREDEFINED.define(_vector);
		PREDEFINED.define(_boolean);
	}

	public GlobalScope getGlobalScope() {
		return GLOBALS;
	}

	public Scope getPredefinedScope() {
		return PREDEFINED;
	}

	public int defineStringLiteral(String s) {
		if (strings.containsKey(s))
			return strings.get(s);
		else
			strings.put(s,++strIndex);
		return strIndex;
	}


	public LinkedHashMap sortHashMapByValues(HashMap map) {
		List mapKeys = new ArrayList(map.keySet());
		List mapValues = new ArrayList(map.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		LinkedHashMap sortedMap = new LinkedHashMap();

		Iterator valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Object val = valueIt.next();
			Iterator keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				Object key = keyIt.next();
				Object keyVal = map.get(key);
				if (keyVal == val) {
					sortedMap.put(key, keyVal);
				}
			}

		}
		return sortedMap;
	}


	public HashMap<String,WFunctionSymbol> getfunctions() {
		HashMap<String,WFunctionSymbol> functions = new HashMap<>();
		for (Symbol s :GLOBALS.getAllSymbols()) {
			if(s instanceof WFunctionSymbol) {
				functions.put(s.getName(),(WFunctionSymbol)s);
			}
		}
		return functions;
	}

	public int computerFuncIndex(String name) {
		int i = -1;
		for (Symbol v :GLOBALS.getSymbols()) {
			if(v instanceof WFunctionSymbol){
				i++;
				if (v.getName().equals(name)){
					return i;
				}
			}
		}
		return i;
	}

	public static String dump(Scope s) {
		StringBuilder buf = new StringBuilder();
		dump(buf, s, 0);
		return buf.toString();
	}

	public static void dump(StringBuilder buf, Scope s, int level) {
		buf.append(tab(level));
		buf.append(s.getName()+" {\n");
		level++;
		for (Symbol sym : s.getSymbols()) {
			if ( !(sym instanceof Scope) ) {
				buf.append(tab(level));	buf.append(sym + "\n");
			}
		}
		for (Scope nested : s.getNestedScopedSymbols()) {
			dump(buf, nested, level);
		}
		if ( s instanceof WBlock ) {
			for (WBlock blk : ((WBlock)s).nestedBlocks) {
				dump(buf, blk, level);
			}
		}
		if ( s instanceof WFunctionSymbol ) {
			dump(buf, ((WFunctionSymbol)s).block, level);
		}
		level--;
		buf.append(tab(level));
		buf.append("}\n");
	}

	public static String tab(int n) {
		StringBuilder buf = new StringBuilder();
		for (int i=1; i<=n; i++) buf.append("    ");
		return buf.toString();
	}

	public static Type op(int op, ExprContext lt, ExprContext rt) {
		return TypeHelper.getResultType(op, lt, rt);
	}

}
