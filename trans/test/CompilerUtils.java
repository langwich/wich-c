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

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import wich.codegen.CodeGenerator;
import wich.codegen.ModelConverter;
import wich.codegen.model.OutputModelObject;
import wich.errors.WichErrorHandler;
import wich.parser.WichLexer;
import wich.parser.WichParser;
import wich.semantics.AssignTypes;
import wich.semantics.CheckTypes;
import wich.semantics.ComputeTypes;
import wich.semantics.DefineSymbols;
import wich.semantics.FinalComputeTypes;
import wich.semantics.SymbolTable;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CompilerUtils {
	public static final Charset FILE_ENCODING = StandardCharsets.UTF_8;

	private static ParserRuleContext parse(ANTLRInputStream antlrInputStream) {
		TokenStream tokens = new CommonTokenStream(new WichLexer(antlrInputStream));
		WichParser parser = new WichParser(tokens);
		return parser.script();
	}

	static ParserRuleContext defineSymbols(String input, SymbolTable symtab, WichErrorHandler err) {
		ParserRuleContext tree = parse(new ANTLRInputStream(input));
		ParseTreeWalker walker = new ParseTreeWalker();
		DefineSymbols defSymbols = new DefineSymbols(symtab, err);
		walker.walk(defSymbols, tree);
		symtab.numOfVars = defSymbols.getNumOfVars();
		return tree;
	}

	static ParserRuleContext getAnnotatedParseTree(String input, SymbolTable symtab, WichErrorHandler err) {
		ParserRuleContext tree = defineSymbols(input, symtab, err);

		ComputeTypes computeTypes = new ComputeTypes(err);
		AssignTypes assignTypes = new AssignTypes(err, symtab.numOfVars);
		ParseTreeWalker walker = new ParseTreeWalker();
		do {
			walker.walk(computeTypes, tree);
			walker = new ParseTreeWalker();
			walker.walk(assignTypes, tree);
		} while(!assignTypes.isAssignFinished);

		FinalComputeTypes finalComputeTypes = new FinalComputeTypes(err);
		walker = new ParseTreeWalker();
		walker.walk(finalComputeTypes, tree);
		return tree;
	}

	static ParserRuleContext checkCorrectness(String input, SymbolTable symtab, WichErrorHandler err) {
		ParserRuleContext tree = getAnnotatedParseTree(input, symtab, err);
		CheckTypes checker = new CheckTypes(err);
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(checker, tree);
		return tree;
	}

	static String genCode(String input, SymbolTable symtab, WichErrorHandler err) {
		ParserRuleContext tree = checkCorrectness(input, symtab, err);
		CodeGenerator codeGenerator = new CodeGenerator(symtab);
		OutputModelObject omo = codeGenerator.generate(tree);
		STGroup templates = new STGroupFile("wich.stg");
		ModelConverter converter = new ModelConverter(templates);
		ST wichST = converter.walk(omo);
		return wichST.render();
	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	static void writeFile(String path, String output, Charset encoding) throws IOException {
		Files.write(Paths.get(path), output.getBytes(encoding));
	}

	static URL getResourceFile(String resName) {
		return WichBaseTest.class.getClassLoader().getResource(resName);
	}
}
