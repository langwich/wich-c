import org.antlr.v4.runtime.misc.Triple;
import org.antlr.v4.runtime.misc.Utils;
import org.junit.Assert;
import org.junit.Test;
import wich.codegen.CompilerUtils;
import wich.codegen.bytecode.BytecodeWriter;
import wich.errors.WichErrorHandler;
import wich.parser.WichParser;
import wich.semantics.SymbolTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

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
public class TestBytecodeInterpreter extends TestWichExecution {
	protected static final String WRUN_DIR = "/usr/local/wich/bin/";

	public TestBytecodeInterpreter(File input, String baseName) {
		super(input, baseName);
	}


	@Test
	public void testCodeGen() throws Exception {
		testCodeGen(CompilerUtils.CodeGenTarget.BYTECODE);
	}

	@Test
	public void testExecution() throws Exception {
		URL expectedFile = CompilerUtils.getResourceFile(baseName + ".output");
		String expected = "";
		if (expectedFile != null) {
			expected = CompilerUtils.readFile(expectedFile.getPath(), CompilerUtils.FILE_ENCODING);
		}
		executeAndCheck(input.getAbsolutePath(), expected, false, CompilerUtils.CodeGenTarget.BYTECODE);
	}

	@Override
	protected void executeAndCheck(String wichFileName,
	                               String expected,
	                               boolean valgrind,
	                               CompilerUtils.CodeGenTarget target)
			throws IOException, InterruptedException
	{
		String wasmFilename = baseName + ".wasm";
		compileWASM(wichFileName, wasmFilename);
		String executable = "./wrun " + WORKING_DIR + wasmFilename;
		System.out.println(executable);
		String actual = executeWASM(wasmFilename);
		assertEquals(expected, actual);

		if ( valgrind ) {
			valgrindCheck(wasmFilename);
		}
	}

	protected void compileWASM(String wichInputFilename, String wasmFilename)
			throws IOException, InterruptedException
	{
		// Translate to .wasm file.
		SymbolTable symtab = new SymbolTable();
		WichErrorHandler err = new WichErrorHandler();
		String wichInput = CompilerUtils.readFile(wichInputFilename, CompilerUtils.FILE_ENCODING);
		WichParser.ScriptContext tree = (WichParser.ScriptContext) CompilerUtils.checkCorrectness(wichInput, symtab, err);
		if ( err.getErrorNum() > 0 ) {
			throw new RuntimeException("failed compilation of "+wichInputFilename+" with error:\n "+ err.toString());
		}
		BytecodeWriter writer = new BytecodeWriter(symtab, tree);
		writer.write(WORKING_DIR+wasmFilename);
	}

	protected String executeWASM(String wasmFilename) throws IOException, InterruptedException {
		Triple<Integer, String, String> result = exec(new String[]{ "./wrun", WORKING_DIR + wasmFilename});
		if ( result.a!=0 ) {
			throw new RuntimeException("failed execution of " + wasmFilename + " with result code "+result.a+"; stderr:\n"+result.c);
		}
		return result.b;
	}

	@Override
	protected Triple<Integer, String, String> exec(String[] cmd) throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder();
		pb.command(Arrays.asList(cmd)).directory(new File(WRUN_DIR));
		Process process = pb.start();
		int resultCode = process.waitFor();
		String stdout = dump(process.getInputStream());
		String stderr = dump(process.getErrorStream());
		Triple<Integer, String, String> ret = new Triple<>(resultCode, stdout, stderr);
		return ret;
	}

	@Override
	protected void valgrindCheck(String wasmFilename) throws IOException, InterruptedException {
		// For Intellij users you need to set PATH environment variable in Run/Debug configuration,
		// since Intellij doesn't inherit environment variables from system.
		String errSummary = exec(new String[]{"valgrind", "./wrun", WORKING_DIR + wasmFilename}).c;
		Assert.assertEquals("Valgrind memcheck failed...", 0, getErrorNumFromSummary(errSummary));
	}
}
