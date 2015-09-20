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

import org.junit.Test;
import wich.semantics.SymbolTable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class TestCodeGen {

	private static final Charset FILE_ENCODING = StandardCharsets.UTF_8;
	private static final String FOLDER = "./test/";

	@Test
	public void testSimpleVarDef() throws Exception {
		genCodeAndCheck("t1.w");
	}

	@Test
	public void testSimpleVecDef() throws Exception {
		genCodeAndCheck("t2.w");
	}

	@Test
	public void testSimpleStrDef() throws Exception {
		genCodeAndCheck("t3.w");
	}

	@Test
	public void testSimpleIfStat() throws Exception {
		genCodeAndCheck("t4.w");
	}

	@Test
	public void testSimpleWhileStat() throws Exception {
		genCodeAndCheck("t5.w");
	}

	@Test
	public void testFunc() throws Exception {
		genCodeAndCheck("t6.w");
	}

	@Test
	public void testFuncReturn() throws Exception {
		genCodeAndCheck("t7.w");
	}

	@Test
	public void testCopyOnWrite() throws Exception {
		genCodeAndCheck("t8.w");
	}

	@Test
	public void testStringAdd() throws Exception {
		genCodeAndCheck("t9.w");
	}

	@Test
	public void testStringAndScope() throws Exception {
		genCodeAndCheck("t10.w");
	}

	@Test
	public void testStringOP() throws Exception {
		genCodeAndCheck("t11.w");
	}

	@Test
	public void testfunCall() throws Exception {
		genCodeAndCheck("t12.w");
	}

	// TODO: use system command to compile and test the c code maybe?
	private void genCodeAndCheck(String inputFileName) throws IOException {
		SymbolTable symtab = new SymbolTable();
		String actual = CompilerFacade.genCode(readFile(FOLDER + inputFileName, FILE_ENCODING), symtab);
		String baseName = inputFileName.substring(0, inputFileName.indexOf('.'));
		String expected = readFile(FOLDER + baseName + "_expected.c", FILE_ENCODING);
		assertEquals(expected, actual);
	}

	String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
