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

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import wich.codegen.ModelConverter;
import wich.codegen.model.OutputModelObject;
import wich.codegen.CodeGenerator;
import wich.parser.*;
import wich.semantics.SymbolTable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
			ParserRuleContext tree = parser.file();

			// trigger tree walk
			SymbolTable symtab = new SymbolTable();
			ParseTreeWalker walker = new ParseTreeWalker();
			SymbolTableConstructor symtabConstructor = new SymbolTableConstructor(symtab);
			walker.walk(symtabConstructor, tree);

			// use the TypeAnnotator listener to compute and
			// and annotate the parse tree with type information
			// also, it deals with type inference and type promotion
			TypeAnnotator typeAnnotator = new TypeAnnotator(symtab);
			walker = new ParseTreeWalker();
			walker.walk(typeAnnotator, tree);

			// use TypeChecker listener to do static type checking
			TypeChecker typeChecker = new TypeChecker(symtab);
			walker = new ParseTreeWalker();
			walker.walk(typeChecker, tree);

			// use CodeGenerator tree visitor to generate the target
			// language (C).
			CodeGenerator codeGenerator = new CodeGenerator(symtab);
			OutputModelObject omo = codeGenerator.visit(tree);

			// using omo and string template to generate translated LLVM bitcode.
			STGroup templates = new STGroupFile("resources/wich.stg");
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
