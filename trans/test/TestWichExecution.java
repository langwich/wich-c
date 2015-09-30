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
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import wich.errors.WichErrorHandler;
import wich.semantics.SymbolTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

// Assuming the program is running on Unix-like operating systems.
// Please make sure gcc is on the program searching path.
public class TestWichExecution extends WichBaseTest {

	protected static final String WORKING_DIR = "/tmp/";
	protected static final String WICH_LIB = "wich.c";
	protected static String runtimePath;

	@Before
	public void setUp() throws Exception {
		URL wichLib = CompilerUtils.getResourceFile(WICH_LIB);
		if (wichLib != null) runtimePath = new File(wichLib.getPath()).getParent();
		else throw new IllegalArgumentException("Can't find wich runtime library.");
	}

	public TestWichExecution(File input, String baseName) {
		super(input, baseName);
	}

	@Test
	public void testCodeGen() throws Exception {
		WichErrorHandler err = new WichErrorHandler();
		SymbolTable symtab = new SymbolTable();
		URL expURL = CompilerUtils.getResourceFile(baseName + ".c");
		assertNotNull(expURL);
		String expPath = expURL.getPath();
		String expected = CompilerUtils.readFile(expPath, CompilerUtils.FILE_ENCODING);
		String contents = CompilerUtils.readFile(input.getAbsolutePath(), CompilerUtils.FILE_ENCODING);
		String actual = CompilerUtils.genCode(contents, symtab, err);
		CompilerUtils.writeFile("/tmp/__t.c", actual, StandardCharsets.UTF_8);

		// normalize the file using gnu indent (brew install gnu-indent on OS X)
		exec(
			new String[] {
				"gindent",
				"-bap", "-bad", "-br", "-nce", "-ncs", "-nprs", "-npcs", "-sai", "-saw",
				"-di1", "-brs", "-blf", "--indent-level4", "-nut", "-sob", "-l200",
				"/tmp/__t.c"
			}
		);
		actual = CompilerUtils.readFile("/tmp/__t.c", StandardCharsets.UTF_8);
//		System.out.println("NORMALIZED\n"+actual);

		// format the expected file as well
		exec(
			new String[] {
				"gindent",
				"-bap", "-bad", "-br", "-nce", "-ncs", "-nprs", "-npcs", "-sai", "-saw",
				"-di1", "-brs", "-blf", "--indent-level4", "-nut", "-sob", "-l200",
				expPath,
				"-o", "/tmp/__expected.c"
			}
		);
		expected = CompilerUtils.readFile("/tmp/__expected.c", StandardCharsets.UTF_8);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testExecution() throws Exception {
		URL expectedFile = CompilerUtils.getResourceFile(baseName + ".output");
		String expected = "";
		if (expectedFile != null) {
			expected = CompilerUtils.readFile(expectedFile.getPath(), CompilerUtils.FILE_ENCODING);
		}
		executeAndCheck(input.getAbsolutePath(), expected);
	}

	private void executeAndCheck(String inputFileName, String expected) throws IOException, InterruptedException {
		String executable = compileC(inputFileName);
		String output = executeC(executable);
		valgrindCheck(executable);
		System.out.println(output);
		assertEquals(expected, output);
	}

	private void valgrindCheck(String executable) throws IOException, InterruptedException {
		// For Intellij users you need to set PATH environment variable in Run/Debug configuration,
		// since Intellij doesn't inherit environment variables from system.
		String errSummary = exec(new String[]{"valgrind", executable}).getValue();
		assertEquals("Valgrind memcheck failed...", 0, getErrorNumFromSummary(errSummary));
	}

	private int getErrorNumFromSummary(String errSummary) {
		if (errSummary == null || errSummary.length() == 0) return -1;
		String[] lines = errSummary.split("\n");
		//Sample: ==15358== ERROR SUMMARY: 0 errors from 0 contexts (suppressed: 0 from 0)
		String summary = lines[lines.length-1];
		return Integer.parseInt(summary.substring(summary.indexOf(":") + 1, summary.lastIndexOf("errors")).trim());
	}

	private String compileC(String wichInput) throws IOException, InterruptedException {
		// Translate to C file.
		SymbolTable symtab = new SymbolTable();
		WichErrorHandler err = new WichErrorHandler();
		String actual = CompilerUtils.genCode(CompilerUtils.readFile(wichInput, CompilerUtils.FILE_ENCODING), symtab, err);
		String generatedFileName = WORKING_DIR + baseName + ".c";
		CompilerUtils.writeFile(generatedFileName, actual, StandardCharsets.UTF_8);
		// Compile C code and return the path to the executable.
		String executable = "./" + baseName;
		final Pair<String, String> result = exec(
			new String[]{
				"cc", "-g", "-o", executable,
				generatedFileName, runtimePath + "/" + WICH_LIB,
				"-I", runtimePath, "-std=c99", "-O0"
			}
		);
		System.out.println(result.getValue());
		return executable;
	}

	private Pair<String, String> exec(String[] cmd) throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder();
		pb.command(Arrays.asList(cmd)).directory(new File(WORKING_DIR));
		Process process = pb.start();
		Pair<String, String> ret = new Pair<>(dump(process.getInputStream()), dump(process.getErrorStream()));
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
