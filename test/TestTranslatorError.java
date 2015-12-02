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
import wich.errors.WichErrorHandler;
import wich.semantics.SymbolTable;

import static junit.framework.Assert.assertEquals;

public class TestTranslatorError {
	@Test
	public void testIncompatibleAssign() throws Exception {
		String input =
				"var x = 1\n" +
				"x = 1.0";
		String expected = "error: line 2:0 incompatible type in assignment (cannot promote from float to int)";
		compileAndCheckError(input, expected);
	}

	@Test
	public void testInvalidElementStr() throws Exception {
		String input =
				"var x = [1,2,3,4,\"5\"]\n";
		String expected = "error: line 1:8 incorrect element type (should be float, but string was given)";
		compileAndCheckError(input, expected);
	}

	@Test
	public void testInvalidIndexFloat() throws Exception {
		String input =
				"var x = [1,2,3,4,5]\n" +
				"x[1.0] = 1\n";
		String expected = "error: line 2:0 invalid vector index type (should be int, but float was given)";
		compileAndCheckError(input, expected);
	}

	@Test
	public void testInvalidIndexVector() throws Exception {
		String input =
				"var x = [1,2,3,4,5]\n" +
				"x[[1]] = 1.0\n";
		String expected = "error: line 2:0 invalid vector index type (should be int, but [] was given)";
		compileAndCheckError(input, expected);
	}

	@Test
	public void testInvalidIndexExpr() throws Exception {
		String input =
				"var x = [1,2,3,4,5]\n" +
				"x[2 > 1] = 1.0\n";
		String expected = "error: line 2:0 invalid vector index type (should be int, but boolean was given)";
		compileAndCheckError(input, expected);
	}

	@Test
	public void testIncompatibleOpFromRet() throws Exception {
		String input =
				"func f() : boolean { return 2 > 1 }\n" +
				"var x = 1 + f()\n";
		String expected =
				"error: line 2:8 incompatible operand types (int + boolean)";
		compileAndCheckError(input, expected);
	}

	@Test
	public void testIncompatibleOpRelational() throws Exception {
		String input =
				"var x = \"1\" > [1.0]\n";
		String expected =
				"error: line 1:8 incompatible operand types (string > [])";
		compileAndCheckError(input, expected);
	}

	@Test
	public void testIncompatibleOpLogical() throws Exception {
		String input =
				"var x = (2 > 1) && 1\n";
		String expected =
				"error: line 1:8 incompatible operand types (boolean && int)";
		compileAndCheckError(input, expected);
	}

	@Test
	public void testInvalidIfCond() throws Exception {
		String input =
				"if (1) { }\n";
		String expected =
				"error: line 1:0 invalid condition type (boolean expected but int was given)";
		compileAndCheckError(input, expected);
	}

	@Test
	public void testInvalidWhileCond() throws Exception {
		String input =
				"while ([1]) { }\n";
		String expected =
				"error: line 1:0 invalid condition type (boolean expected but [] was given)";
		compileAndCheckError(input, expected);
	}

	@Test
	public void testIncompatibleFuncArgs() throws Exception {
		String input =
				"func foo(x:float,y:int, c:boolean):int { }\n" +
				"foo(1, 1.0, 2)\n";
		String expected =
				"error: line 2:0 incompatible argument type (cannot promote from float to int)\n" +
						" error: line 2:0 incompatible argument type (cannot promote from int to boolean)";
		compileAndCheckError(input, expected);
	}

	@Test
	public void testIncorrectNumOfArgs() throws Exception {
		String input =
				"func foo(x:float,y:int, c:boolean):int { }\n" +
						"foo(1.0, 1)\n";
		String expected =
				"error: line 2:0 incorrect number of args (3 args expected but 2 was given)";
		compileAndCheckError(input, expected);
	}
	@Test
	public void testInvalidTypeFuncArgs() throws Exception {
		String input =
				"func foo(x:float,y:int,c:Boolean):int { }\n" ;
		String expected =
				"error: line 1:25 invalid syntax: no viable alternative at input 'Boolean'" ;
		compileAndCheckError(input, expected);
	}

	@Test
	public void testInvokeOnVar() throws Exception {
		String input =
				"var x = 1\n" +
				"x()\n";
		String expected =
				"error: line 2:0 function x not defined";
		compileAndCheckError(input, expected);
	}

	@Test
	public void testIndexOnNonVector() throws Exception {
		String input =
				"var x = 1\n" +
				"x[1] = 1\n";
		String expected =
				"error: line 2:0 invalid operation ([] expected, but int was given)";
		compileAndCheckError(input, expected);
	}

	@Test
	public void testReturnTypeError() throws Exception {
		String input =
				"func f():float {\n" +
					"return 34\n" +
				"}\n";
		String expected =
				"error: line 2:0 invalid return type int, float was expected";
		compileAndCheckError(input, expected);
	}

	@Test
	public void testTermination() throws Exception {
		String input =
				"var x = y\n";
		String expected =
				"error: line 1:8 symbol not found (y)";
		compileAndCheckError(input, expected);
	}
	private void compileAndCheckError(String input, String expected) {
		SymbolTable symtab = new SymbolTable();
		WichErrorHandler errorHandler = new WichErrorHandler();
		CompilerUtils.checkCorrectness(input, symtab, errorHandler);
		assertEquals(expected, errorHandler.toString());
	}
}
