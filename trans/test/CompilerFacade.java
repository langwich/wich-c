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

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import wich.parser.*;
import wich.semantics.SymbolTable;

public class CompilerFacade {

	private static ParserRuleContext parse(ANTLRInputStream antlrInputStream) {
		TokenStream tokens = new CommonTokenStream(new WichLexer(antlrInputStream));
		WichParser parser = new WichParser(tokens);
		return parser.file();
	}

	public static ParserRuleContext defineSymbols(String input, SymbolTable symtab) {
		ParserRuleContext tree = parse(new ANTLRInputStream(input));
		ParseTreeWalker walker = new ParseTreeWalker();
		SymbolTableConstructor symtabConstructor = new SymbolTableConstructor(symtab);
		walker.walk(symtabConstructor, tree);
		return tree;
	}

	public static ParserRuleContext getAnnotatedParseTree(String input, SymbolTable symtab) {
		ParserRuleContext tree = defineSymbols(input, symtab);
		TypeAnnotator typeAnnotator = new TypeAnnotator(symtab);
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(typeAnnotator, tree);
		return tree;
	}
}
