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

import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;
import wich.semantics.SymbolTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

// Assuming the program is running on Unix-like operating systems.
// Please make sure gcc is on the program searching path.
// TODO: Add valgrind check
public class TestWichExecution {

	protected static final String WORKING_DIR = "/tmp/";
	protected static final String RUNTIME_DIR = "../../../runtime/src/";
	protected static String folder = CompilerFacade.FOLDER;
	protected static Charset encoding = CompilerFacade.FILE_ENCODING;
	protected static String runtimePath;

	@Before
	public void setUp() throws Exception {
		// find the include file we need from classpath. Make sure
		// intellij or whatever knows runtime/src dir is a resources dir
		URL which_h = getClass().getClassLoader().getResource("wich.h");
		if ( which_h!=null ) {
			runtimePath = new File(which_h.getPath()).getParent();
		}
		else {
			throw new IllegalArgumentException("Can't find which.h directory");
		}
	}

	@Test
	public void testIfStat() throws Exception {
		executeAndCheck("t4.w", "TRUE\n");
	}

	@Test
	public void testLoop() throws Exception {
		executeAndCheck("t5.w",
				"11.00\n" +
				"10.00\n" +
				"9.00\n" +
				"8.00\n" +
				"7.00\n" +
				"6.00\n" +
				"5.00\n" +
				"4.00\n" +
				"3.00\n" +
				"2.00\n");
	}

	@Test
	public void testRecursion() throws Exception {
		executeAndCheck("t6.w", "5\n");
	}

	@Test
	public void testVectorPrint() throws Exception {
		executeAndCheck("t7.w", "[1.00, 2.00, 3.00, 4.00, 5.00]\n");
	}

	@Test
	public void testArgumentPassing() throws Exception {
		executeAndCheck("t8.w",
				"[100.00, 2.00, 3.00]\n" +
				"[99.00, 2.00, 3.00]\n");
	}

	@Test
	public void testStringConcat() throws Exception {
		executeAndCheck("t9.w",
				"superman\n" +
				"superduper\n");
	}

	private void executeAndCheck(String inputFileName, String expected) throws IOException, InterruptedException {
		String executable = compileC(inputFileName);
		String output = executeC(executable);
		assertEquals(expected, output);
	}

	private String compileC(String wichInput) throws IOException, InterruptedException {
		// Translate to C file.
		SymbolTable symtab = new SymbolTable();
		URL WichInputURL = getClass().getClassLoader().getResource(wichInput);
		String actual = CompilerFacade.genCode(CompilerFacade.readFile(WichInputURL.getPath(), encoding), symtab);
		String baseName = wichInput.substring(0, wichInput.indexOf('.'));
		String generated = WORKING_DIR + baseName + "_wich.c";
		CompilerFacade.writeFile(generated, actual, StandardCharsets.UTF_8);
		// Compile C code and return the path to the executable.
		String executable = baseName + "_wich";
		URL CFileURL = getClass().getClassLoader().getResource(wichInput);
		exec(new String[]{"cc", "-o", executable, generated, CFileURL.getFile(), "-I", runtimePath, "-std=c99"});
		return executable;
	}

	private Pair<String, String> exec(String[] cmd) throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder();
		pb.command(Arrays.asList(cmd)).directory(new File(WORKING_DIR));
		Process process = pb.start();
		Pair<String, String> ret = new Pair<>(dump(process.getInputStream()), dump(process.getErrorStream()));
		System.out.println(ret.getValue());
		process.waitFor();
		return ret;
	}

	private String dump(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuilder out = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			out.append(line);
			out.append(System.getProperty("line.separator"));
		}
		return out.toString();
	}

	private String executeC(String executable) throws IOException, InterruptedException {
		return exec(new String[]{"./" + executable}).getKey();
	}
}
