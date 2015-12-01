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
import org.junit.runners.Parameterized;
import wich.codegen.CompilerUtils;
import wich.errors.WichErrorHandler;
import wich.semantics.SymbolTable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class TestWichError extends TestWichExecution {
	protected static final String TEST_ERR = TEST_RES + "/error";

	public TestWichError(File input, String baseName) {
		super(input, baseName);
	}

	@Test
	public void testErrorHandling() throws IOException, InterruptedException {
		WichErrorHandler err = new WichErrorHandler();
		SymbolTable symtab = new SymbolTable();
		assertTrue(err.toString(), err.getErrorNum()==0);

		String wichInput = CompilerUtils.readFile(input.getAbsolutePath(), CompilerUtils.FILE_ENCODING);
		CompilerUtils.genCode(wichInput, symtab, err, CompilerUtils.CodeGenTarget.PLAIN);

		URL expectedOutputURL = CompilerUtils.getResourceFile(TEST_ERR+"/"+baseName+".output");
		assertNotNull(expectedOutputURL);

		String expPath = expectedOutputURL.getPath();
		String expected = CompilerUtils.readFile(expPath, CompilerUtils.FILE_ENCODING);
		if (err.getErrorNum() > 0) {assertEquals(expected, err.toString()); return;}

		executeAndCheck(input.getAbsolutePath(), expected, false, CompilerUtils.CodeGenTarget.PLAIN);
	}

	@Parameterized.Parameters(name="{1}")
	public static Collection<Object[]> findInputFiles() {
		return findTestCasesInFolder(TEST_ERR);
	}

}
