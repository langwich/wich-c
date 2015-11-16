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

import wich.codegen.CompilerUtils;
import wich.errors.WichErrorHandler;
import wich.parser.WichParser;
import wich.semantics.SymbolTable;

import java.io.FileOutputStream;

/*
~/github/wich-c/test/samples $ for f in *.w; do echo $f;
java -cp $CLASSPATH:../../out/production/symtab:../../out/production/wich-c wich.Trans $f -target MARK_AND_COMPACT -o "gc/${f%%.*}.c";
done
 */

public class Trans {
	public static void main(String[] args) throws Exception {
		Trans translator = new Trans();
		if ( args.length!=1 && args.length!=3 && args.length!=5 ) {
			System.out.println("Try: java wich.Trans <input_file> [-target targetname] [-o <output_path>]");
			return;
		}

		String inputFile = args[0];
		String outputFilename = null;
		String target = "PLAIN";
		int i = 1;
		while ( i<args.length ) {
			switch ( args[i] ) {
				case "-o":
					outputFilename = args[i+1];
					break;
				case "-target":
					target = args[i+1];
					break;
				default:
					System.out.println("Try: java wich.Trans <input_file> [-target targetname] [-o <output_path>]");
					return;
			}
			i += 2;
		}
		translator.translate(inputFile, target, outputFilename);
	}

	public void translate(String filename, String targetS, String outputFilename)
		throws Exception
	{
		SymbolTable symtab = new SymbolTable();
		WichErrorHandler err = new WichErrorHandler();
		String wichInput = CompilerUtils.readFile(filename, CompilerUtils.FILE_ENCODING);
		CompilerUtils.CodeGenTarget target = CompilerUtils.CodeGenTarget.valueOf(targetS);
		String code = CompilerUtils.genCode(wichInput, symtab, err, target);
		// print out result to console.
		if (outputFilename!=null) {
			FileOutputStream fos = new FileOutputStream(outputFilename);
			fos.write(code.getBytes());
			fos.close();
		}
		else {
			System.out.println(code);
		}
	}

	public WichParser.ScriptContext semanticsPhase(String lava,SymbolTable symtab) {
		WichErrorHandler err = new WichErrorHandler();
		return (WichParser.ScriptContext)CompilerUtils.checkCorrectness(lava,symtab,err);

	}
}
