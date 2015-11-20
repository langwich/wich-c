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
import wich.codegen.CompilerUtils;

import java.io.File;
import java.net.URL;

public class TestLLVMMarkAndCompact extends TestWichExecution {

	public TestLLVMMarkAndCompact(File input, String baseName) {
		super(input, baseName);
	}

	@Test
	public void testCodeGen() throws Exception {
		testCodeGen(CompilerUtils.CodeGenTarget.LLVM_MARK_AND_COMPACT);
	}

	@Test
	public void testExecution() throws Exception {
		URL expectedFile = CompilerUtils.getResourceFile(baseName + ".output");
		String expected = "";
		if (expectedFile != null) {
			expected = CompilerUtils.readFile(expectedFile.getPath(), CompilerUtils.FILE_ENCODING);
		}
		executeAndCheck(input.getAbsolutePath(), expected, false, CompilerUtils.CodeGenTarget.LLVM_MARK_AND_COMPACT);
	}
}