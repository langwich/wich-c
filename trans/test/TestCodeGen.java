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
import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

public class TestCodeGen {

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

	@Test
	public void testForwardFuncRef() throws Exception {
		genCodeAndCheck("t13.w");
	}

	// TODO: use system command to compile and test the c code maybe?
	private void genCodeAndCheck(String inputFileName) throws IOException {
		Charset encoding = CompilerFacade.FILE_ENCODING;
		SymbolTable symtab = new SymbolTable();
		URL WichFileURL = getClass().getClassLoader().getResource(inputFileName);
		String actual = CompilerFacade.genCode(CompilerFacade.readFile(WichFileURL.getPath(), encoding), symtab);
		String baseName = inputFileName.substring(0, inputFileName.indexOf('.'));
		URL CfileURL = getClass().getClassLoader().getResource(baseName + "_expected.c");
		String expected = CompilerFacade.readFile(CfileURL.getPath(), encoding);
		assertEquals(expected, actual);
	}

}
