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

package wich;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import wich.codegen.CodeGenerator;
import wich.codegen.ModelConverter;
import wich.codegen.model.File;
import wich.errors.WichErrorHandler;
import wich.parser.WichLexer;
import wich.parser.WichParser;
import wich.semantics.AssignTypes;
import wich.semantics.CheckTypes;
import wich.semantics.ComputeTypes;
import wich.semantics.DefineSymbols;
import wich.semantics.FinalComputeTypes;
import wich.semantics.SymbolTable;

import java.io.FileOutputStream;
import java.io.IOException;

public class Trans {

	public static void main(String[] args) {
		Trans translator = new Trans();
		if (args.length != 2 && args.length != 3) {
			System.out.println("Try: java Trans <input_file> [-console | -file <output_path>]");
			return;
		}

		String inputFile;
		String outputPath = null;
		boolean console = false;
		boolean file = false;
		switch (args[0]) {
			case "-console":
				console = true;
				break;
			case "-file":
				file = true;
				outputPath = args[2];
				break;
			default:
				break;
		}
		inputFile = args[0];
		translator.translate(inputFile, console, file, outputPath);
	}

	public void translate(String filename, boolean console, boolean file, String outputPath) {
		try {
			// get the token stream
			ANTLRInputStream input = new ANTLRFileStream(filename);
			WichLexer lexer = new WichLexer(input);
			TokenStream tokens = new CommonTokenStream(lexer);

			// get the parse tree
			WichParser parser = new WichParser(tokens);
			ParserRuleContext tree = parser.script();

			// trigger tree walk to define symbols
			SymbolTable symtab = new SymbolTable();
			WichErrorHandler err = new WichErrorHandler();
			ParseTreeWalker walker = new ParseTreeWalker();
			DefineSymbols defSymbols = new DefineSymbols(symtab, err);
			walker.walk(defSymbols, tree);
			symtab.numOfVars = defSymbols.getNumOfVars();

			/*
			use the listener to compute and
			and annotate the parse tree with type information
			also, it deals with type inference and type promotion
			to support forward reference, iterative passes are used.
			*/

			ComputeTypes computeTypes = new ComputeTypes(err);
			AssignTypes assignTypes = new AssignTypes(err,symtab.numOfVars );
			walker = new ParseTreeWalker();

			do {
				walker.walk(computeTypes, tree);
				walker = new ParseTreeWalker();
				walker.walk(assignTypes, tree);
			} while(!assignTypes.isAssignFinished);

			FinalComputeTypes finalComputeTypes = new FinalComputeTypes(err);
			walker = new ParseTreeWalker();
			walker.walk(finalComputeTypes, tree);

			// use TypeChecker listener to do static type checking
			CheckTypes typeChecker = new CheckTypes(err);
			walker = new ParseTreeWalker();
			walker.walk(typeChecker, tree);

			// use CodeGenerator tree visitor to generate the target
			// language (C).
			CodeGenerator codeGenerator = new CodeGenerator(symtab);
			File omo = codeGenerator.generate(tree);

			// using omo and string template to generate translated code
			STGroup templates = new STGroupFile("wich.stg");
			ModelConverter converter = new ModelConverter(templates);
			ST wichST = converter.walk(omo);
			String wichC = wichST.render();

			// print out result to console.
			if (console) {
				System.out.println(wichC);
			}
			if (file) {
				String baseName = filename.substring(filename.lastIndexOf("/") + 1);
				String unSuffixedName = baseName.substring(0, baseName.indexOf("."));
				FileOutputStream fos = new FileOutputStream(outputPath + "/" + unSuffixedName);
				fos.write(wichC.getBytes());
			}

		} catch (IOException | RecognitionException e) {
			e.printStackTrace();
		}
	}
}
