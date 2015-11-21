import junit.framework.Assert;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Triple;
import org.antlr.v4.runtime.misc.Utils;
import org.junit.Before;
import org.junit.Test;
import wich.Trans;
import wich.codegen.CompilerUtils;
import wich.codegen.bytecode.BytecodeWriter;
import wich.errors.ErrorType;
import wich.errors.WichErrorHandler;
import wich.parser.WichParser;
import wich.semantics.SymbolTable;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

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
public class TestBytecodeInterpreter extends WichBaseTest{
	protected static final String WORKING_DIR = "/tmp/";
	protected static final String LIB_DIR = "/usr/local/wich/lib";
	protected static final String INCLUDE_DIR = "/usr/local/wich/include";
	protected CompilerUtils.MallocImpl mallocImpl = CompilerUtils.MallocImpl.SYSTEM; // default malloc;

	public TestBytecodeInterpreter(File input, String baseName) {
		super(input, baseName);
	}


	@Test
	public void testBytecodeExecution() throws Exception {
		URL expectedFile = CompilerUtils.getResourceFile(TEST_RES + baseName + ".output");
		String expected = "";
		if (expectedFile != null) {
			expected = CompilerUtils.readFile(expectedFile.getPath(), CompilerUtils.FILE_ENCODING);
		}
		executeAndCheck(input.getAbsolutePath(), expected, false);
	}

	protected void executeAndCheck(String wichFileName,
	                               String expected,
	                               boolean valgrind)
			throws IOException, InterruptedException
	{
		String executable = getWASM(wichFileName);
		String output = executeWASM(executable);
		System.out.println(output);
		assertEquals(expected, output);
		if ( valgrind ) {
			valgrindCheck(executable);
		}
	}

	@Before
	public void setUp() throws Exception {
		File dir = new File(LIB_DIR);
		if ( !dir.exists() ) {
			throw new IllegalArgumentException("Can't find wich runtime library.");
		}
	}


	protected void valgrindCheck(String executable) throws IOException, InterruptedException {
		// For Intellij users you need to set PATH environment variable in Run/Debug configuration,
		// since Intellij doesn't inherit environment variables from system.
		String errSummary = exec(new String[]{"valgrind", executable}).c;
		assertEquals("Valgrind memcheck failed...", 0, getErrorNumFromSummary(errSummary));
	}

	protected int getErrorNumFromSummary(String errSummary) {
		if (errSummary == null || errSummary.length() == 0) return -1;
		String[] lines = errSummary.split("\n");
		String summary = lines[lines.length-1];
		return Integer.parseInt(summary.substring(summary.indexOf(":") + 1, summary.lastIndexOf("errors")).trim());
	}

	protected String getWASM(String wichInputFilename)
			throws IOException, InterruptedException
	{
		// Translate to .wasm file.
		Trans tool = new Trans();
		SymbolTable symtab = new SymbolTable();
		WichErrorHandler err = new WichErrorHandler();
		String wichInput = CompilerUtils.readFile(wichInputFilename, CompilerUtils.FILE_ENCODING);
		WichParser.ScriptContext tree = (WichParser.ScriptContext) CompilerUtils.checkCorrectness(wichInput, symtab, err);
		BytecodeWriter gen = new BytecodeWriter("foo", tool, symtab,tree);
		String actual = gen.generateObjectFile();
		actual = actual.replaceAll("\t", "");
		assertTrue(err.toString(), err.getErrorNum()==0);
		String generatedFileName = WORKING_DIR + baseName + ".wasm";
		CompilerUtils.writeFile(generatedFileName, actual, StandardCharsets.UTF_8);

		String executable = "./" + baseName;
		File execF = new File(executable);
		if ( execF.exists() ) {
			execF.delete();
		}

		//how to construct the cmd ?
		//wrun  /Users/yuanyuan/Dropbox/newruntime/runtime/vm/test/samples/alter_vector_arg.wasm

		List<String> cc = new ArrayList<>();
		cc.addAll(
				Arrays.asList(
						"cc", "-g", "-o", executable,
						generatedFileName,
						"-L", LIB_DIR,
						"-I", INCLUDE_DIR, "-std=c99", "-O0"
				)
		);
		for (String lib : target.libs) {
			cc.add("-l"+lib);
		}
		String[] cmd = cc.toArray(new String[cc.size()]);
		if ( mallocImpl!=CompilerUtils.MallocImpl.SYSTEM ) {
			cc.addAll(Arrays.asList("-l"+mallocImpl.lib, "-lmalloc_common"));
		}
		final Triple<Integer, String, String> result = exec(cmd);
		String cmdS = Utils.join(cmd, " ");
		System.out.println(cmdS);
		if ( result.a!=0 ) {
			throw new RuntimeException("failed compilation of "+generatedFileName+" with result code "+result.a+
					" from\n"+
					cmdS+"\nstderr:\n"+result.c);
		}

		return executable;

	}

	protected Triple<Integer, String, String> exec(String[] cmd) throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder();
		pb.command(Arrays.asList(cmd)).directory(new File(WORKING_DIR));
		Process process = pb.start();
		int resultCode = process.waitFor();
		String stdout = dump(process.getInputStream());
		String stderr = dump(process.getErrorStream());
		Triple<Integer, String, String> ret = new Triple<>(resultCode, stdout, stderr);
		return ret;
	}

	protected String dump(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuilder out = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			out.append(line);
			out.append(System.getProperty("line.separator"));
		}
		return out.toString();
	}

	protected String executeWASM(String executable) throws IOException, InterruptedException {
		Triple<Integer, String, String> result = exec(new String[]{"./"+executable});
		if ( result.a!=0 ) {
			throw new RuntimeException("failed execution of "+executable+" with result code "+result.a+"; stderr:\n"+result.c);
		}
		return result.b;
	}

}
