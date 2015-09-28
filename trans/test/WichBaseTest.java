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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import wich.errors.WichErrorHandler;
import wich.semantics.SymbolTable;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class WichBaseTest {

	public static final String TEST_RES = "samples";
	protected File input;
	protected String baseName;

	public WichBaseTest(File input, String baseName) {
		this.input = input;
		this.baseName = baseName;
	}

	@Test
	public void testCodeGen() throws Exception {
		WichErrorHandler err = new WichErrorHandler();
		SymbolTable symtab = new SymbolTable();
		URL expURL = CompilerUtils.getResourceFile(baseName + "_expected.c");
		String expPath = expURL.getPath();
		String expected = CompilerUtils.readFile(expPath, CompilerUtils.FILE_ENCODING);
		String contents = CompilerUtils.readFile(input.getAbsolutePath(), CompilerUtils.FILE_ENCODING);
		String actual = CompilerUtils.genCode(contents, symtab, err);
		assertEquals(expected, actual);
	}

	@Parameterized.Parameters(name="{1}")
	public static Collection<Object[]> findInputFiles() {
		URL testFolder = CompilerUtils.getResourceFile(TEST_RES);
		Collection<Object[]> result = new ArrayList<>();
		// only feed test methods with wich source files.
		String regexp = "^\\w+\\.w$";
		Pattern pattern = Pattern.compile(regexp);
		for (File file : new File(testFolder.getPath()).listFiles(f -> pattern.matcher(f.getName()).matches())) {
			Object[] args = new Object[2];
			args[0] = file;
			args[1] = file.getName().substring(0, file.getName().indexOf("."));
			result.add(args);
		}
		return result;
	}

}