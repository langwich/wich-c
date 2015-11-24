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

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.misc.STMessage;
import wich.codegen.bytecode.BytecodeWriter;
import wich.codegen.model.File;
import wich.errors.ErrorType;
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

	public static final int VOID_TYPE = 0;
	public static final int INT_TYPE = 1;
	public static final int FLOAT_TYPE	= 2;
	public static final int BOOLEAN_TYPE = 3;
	public static final int STRING_TYPE = 4;
	public static final int VECTOR_TYPE = 5;
	/*
-- Installing: /usr/local/wich/lib/libmalloc_common.a
-- Installing: /usr/local/wich/lib/libfreelist.a
-- Installing: /usr/local/wich/lib/libbitmap.a
-- Installing: /usr/local/wich/lib/libbytemap.a
-- Installing: /usr/local/wich/lib/libbinning.a
-- Installing: /usr/local/wich/lib/libmerging.a
-- Installing: /usr/local/wich/lib/libwlib.a
-- Installing: /usr/local/wich/lib/libwlib_refcounting.a
-- Installing: /usr/local/wich/lib/libwlib_mark_and_compact.a
-- Installing: /usr/local/wich/lib/libwlib_mark_and_sweep.a
-- Installing: /usr/local/wich/lib/libcunit.a
-- Installing: /usr/local/wich/lib/libgc_mark_and_compact.a
-- Installing: /usr/local/wich/lib/libgc_mark_and_sweep.a
-- Installing: /usr/local/wich/lib/libmark_and_compact.a
-- Installing: /usr/local/wich/lib/libmark_and_sweep.a
	 */
	public enum CodeGenTarget {
		PLAIN(new String[]{"wlib"}, ".c"),
		LLVM(new String[]{"wlib"}, ".ll"),
		LLVM_MARK_AND_COMPACT(new String[]{"wlib_mark_and_compact", "mark_and_compact", "gc_mark_and_compact", "malloc_common"}, ".ll"),
		LLVM_MARK_AND_SWEEP(new String[]{"wlib_mark_and_sweep", "mark_and_sweep", "gc_mark_and_sweep", "malloc_common"}, ".ll"),
		REFCOUNTING(new String[]{"wlib_refcounting"}, ".c"),
		MARK_AND_COMPACT(new String[]{"wlib_mark_and_compact", "mark_and_compact", "gc_mark_and_compact", "malloc_common"}, ".c"),
		MARK_AND_SWEEP(new String[]{"wlib_mark_and_sweep", "mark_and_sweep", "gc_mark_and_sweep", "malloc_common"}, ".c"),
		SCAVENGER(new String[]{"wlib"}, ".c"),
		BYTECODE(new String[]{}, ".wasm");

		public String[] libs;
		public String flag;
		public String fileExtension; // ".c" for stuff like script.c, script.wasm, etc...

		CodeGenTarget(String[] libs, String fileExtension) {
			this.libs = libs;
			this.flag = this.toString();
			this.fileExtension = fileExtension;
		}
	}

	public enum MallocImpl {
		SYSTEM("system"),
		FREELIST("freelist"),
		BITMAP("bitmap"),
		BYTEMAP("bytemap"),
		BINNING("binning"),
		MERGING("merging");

		public String lib;

		MallocImpl(String lib) {
			this.lib = lib;
		}
	}

	public static final Charset FILE_ENCODING = StandardCharsets.UTF_8;

	public  static ParserRuleContext parse(ANTLRInputStream antlrInputStream, WichErrorHandler err) {
		TokenStream tokens = new CommonTokenStream(new WichLexer(antlrInputStream));
		WichParser parser = new WichParser(tokens);
		parser.removeErrorListeners();
		int[] errors = new int[1];
		BaseErrorListener antlrListener = new BaseErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
			                        int line, int charPositionInLine,
			                        String msg, RecognitionException e) {
				err.error((Token) offendingSymbol, ErrorType.SYNTAX_ERROR, e, msg);
				errors[0]++;
			}
		};
		parser.addErrorListener(antlrListener);
		WichParser.ScriptContext tree = parser.script();
		if ( errors[0]>0 ) return null;
		return tree;
	}

	public static ParserRuleContext defineSymbols(String input, SymbolTable symtab, WichErrorHandler err) {
		ParserRuleContext tree = parse(new ANTLRInputStream(input), err);
		if ( tree==null ) return null;
		ParseTreeWalker walker = new ParseTreeWalker();
		DefineSymbols defSymbols = new DefineSymbols(symtab, err);
		walker.walk(defSymbols, tree);
		symtab.numOfVars = defSymbols.getNumOfVars();
		return tree;
	}

	public static ParserRuleContext getAnnotatedParseTree(String input, SymbolTable symtab, WichErrorHandler err) {
		ParserRuleContext tree = defineSymbols(input, symtab, err);
		if ( tree==null ) return null;

		ComputeTypes computeTypes = new ComputeTypes(err);
		AssignTypes assignTypes = new AssignTypes(err, symtab.numOfVars);
		ParseTreeWalker walker = new ParseTreeWalker();
		do {
			walker.walk(computeTypes, tree);
			walker = new ParseTreeWalker();
			walker.walk(assignTypes, tree);
		} while(!assignTypes.isAssignFinished() && err.getErrorNum() == 0);

		FinalComputeTypes finalComputeTypes = new FinalComputeTypes(err);
		walker = new ParseTreeWalker();
		walker.walk(finalComputeTypes, tree);
		return tree;
	}

	public static ParserRuleContext checkCorrectness(String input, SymbolTable symtab, WichErrorHandler err) {
		ParserRuleContext tree = getAnnotatedParseTree(input, symtab, err);
		if ( tree==null ) return null;
		CheckTypes checker = new CheckTypes(err);
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(checker, tree);
		return tree;
	}

	public static String genCode(String input, SymbolTable symtab, WichErrorHandler err,
	                      CompilerUtils.CodeGenTarget target)
	{
		ParserRuleContext tree = checkCorrectness(input, symtab, err);
		if ( tree==null || err.getErrorNum()>0) return "<invalid>";

		if ( target==CodeGenTarget.BYTECODE ) {
			BytecodeWriter gen = new BytecodeWriter("unused.wasm", symtab, (WichParser.ScriptContext)tree);
			return gen.genObjectFile();
		}

		CodeGenerator codeGenerator = new CodeGenerator(symtab);
		File modelRoot = codeGenerator.generate(tree);
		STGroup templates;
		switch ( target ) {
			case PLAIN :
				templates = new STGroupFile("wich.stg");
				break;
			case LLVM :
				ModelWalker modelWalker = new ModelWalker(new InjectLLVMTraits());
				modelWalker.walk(modelRoot);
				templates = new STGroupFile("wich-llvm.stg");
				break;
			case LLVM_MARK_AND_COMPACT:
				modelWalker = new ModelWalker(new InjectLLVMTraits());
				modelWalker.walk(modelRoot);
				templates = new STGroupFile("wich-llvm-mc.stg");
				break;
			case LLVM_MARK_AND_SWEEP:
				modelWalker = new ModelWalker(new InjectLLVMTraits());
				modelWalker.walk(modelRoot);
				templates = new STGroupFile("wich-llvm-ms.stg");
				break;
			case REFCOUNTING :
				modelWalker = new ModelWalker(new InjectRefCounting());
				modelWalker.walk(modelRoot);
				templates = new STGroupFile("wich-refcounting.stg");
				break;
			case MARK_AND_COMPACT:
			case MARK_AND_SWEEP:
			case SCAVENGER:
				templates = new STGroupFile("wich-gc.stg");
				break;
			default :
				err.error(null, ErrorType.UNKNOWN_TARGET, target.toString());
				return "";
		}

		// model is complete, convert to template hierarchy then string
		ModelConverter converter = new ModelConverter(templates);
		templates.setListener(
			new STErrorListener() {
				@Override
				public void compileTimeError(STMessage stMessage) {
					error(stMessage);
				}
				@Override
				public void runTimeError(STMessage stMessage) {
					error(stMessage);
				}
				@Override
				public void IOError(STMessage stMessage) {
					error(stMessage);
				}
				@Override
				public void internalError(STMessage stMessage) {
					error(stMessage);
				}
				protected void error(STMessage stMessage) {
					ErrorType etype = ErrorType.INTERNAL_STRINGTEMPLATE_ERROR;
					ST template = new ST(etype.getMessageTemplate());
					template.add("arg1", stMessage.toString());
					err.error(template.render(), etype);
				}
			}
		);
		ST wichST = converter.walk(modelRoot);
		return wichST.render();
	}

	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public static void writeFile(String path, String output, Charset encoding) throws IOException {
		Files.write(Paths.get(path), output.getBytes(encoding));
	}

	public static URL getResourceFile(String resName) {
		return CompilerUtils.class.getClassLoader().getResource(resName);
	}


	public static String stripBrackets(String s) {
		return s.substring(1, s.length() - 1);
	}

	/** e.g., replaceFileSuffix("foo.om", ".java") */
	public static String replaceFileSuffix(String s, String suffix) {
		if ( s==null || suffix==null ) return s;
		int dot = s.lastIndexOf('.');
		return s.substring(0,dot)+suffix;
	}

	public static String stripFirstLast(String s) {
		return s.substring(1,s.length()-1);
	}
}

