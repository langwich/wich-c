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

import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.Test;
import wich.codegen.CompilerUtils;
import wich.errors.WichErrorHandler;
import wich.semantics.SymbolTable;
import wich.semantics.TypeHelper;

import static org.junit.Assert.assertEquals;

public class TestTypeAnnotation {
	@Test
	public void testDefineVar() throws Exception {
		String input = "var x = 1";
		String expected =
				"1:int\n";
		annotateTypeAndCheck(input, expected);
	}

	@Test
	public void testOpExpr() throws Exception {
		String input = "var x = 1+2";
		String expected =
				"1:int\n" +
				"2:int\n" +
				"+:int\n";
		annotateTypeAndCheck(input, expected);
	}

	@Test
	public void testThreeOperands() throws Exception {
		String input = "var x = 1+2*3";
		String expected =
				"1:int\n" +
				"2:int\n" +
				"+:int\n" +
				"3:int\n" +
				"*:int\n";
		annotateTypeAndCheck(input, expected);
	}

	@Test
	public void testIfStat() throws Exception {
		String input =
				"var x = 1\n" +
				"var y = 2\n" +
				"if(y > x) {\n" +
				"   var z = 3" +
				"}\n";
		String expected =
				"1:int\n" +
				"2:int\n" +
				"y:int\n" +
				"x:int\n" +
				">:boolean\n" +
				"3:int\n";
		annotateTypeAndCheck(input, expected);
	}

	@Test
	public void testWhile() throws Exception {
		String input =
				"var x = 1\n" +
				"var y = 2\n" +
				"while(y >= x) {\n" +
				"   var z = 3" +
				"}\n";
		String expected =
				"1:int\n" +
				"2:int\n" +
				"y:int\n" +
				"x:int\n" +
				">=:boolean\n" +
				"3:int\n";
		annotateTypeAndCheck(input, expected);
	}

	@Test
	public void testAssign() throws Exception {
		String input =
				"var x = 1\n" +
				"var y = 2\n" +
				"y = x\n";
		String expected =
				"1:int\n" +
				"2:int\n" +
				"x:int\n";
		annotateTypeAndCheck(input, expected);
	}

	@Test
	public void testAssignPromote() throws Exception {
		String input =
				"var x = 1\n" +
				"var y = 0.0\n" +
				"y = x\n";
		String expected =
				"1:int\n"+
				"0.0:float\n"+
				"x:int => float\n";
		annotateTypeAndCheck(input, expected);
	}

	@Test
	public void testElementAssign() throws Exception {
		String input =
				"var arr = [1, 2, 3, 4, 5]\n" +
				"arr[3] = 1\n";
		String expected =
				"[1,2,3,4,5]:[]\n" +
				"1:int => float\n" +
				"2:int => float\n" +
				"3:int => float\n" +
				"4:int => float\n" +
				"5:int => float\n" +
				"3:int\n" +
				"1:int => float\n";
		annotateTypeAndCheck(input, expected);
	}

	@Test
	public void testArgElementAssign() throws Exception {
		String input =
				"func bar(x:[]) {\n"+
				"    x[1] = 100\n" +
				"}\n";
		String expected =
				"1:int\n"+
				"100:int => float\n";
		annotateTypeAndCheck(input, expected);
	}

	@Test
	public void testOpTypePromotion() throws Exception {
		String input = "var x = 1.2 + 1";
		String expected =
				"1.2:float\n" +
				"1:int => float\n" +
				"+:float => float\n";
		annotateTypeAndCheck(input, expected);
	}

	@Test
	public void testRecursion() throws Exception {
		String input =
				"func fib(x:int) : int {\n" +
				"   if ((x == 0) || (x == 1)) {\n" +
				"       return x\n" +
				"   }\n" +
				"   return fib(x-1) + fib(x-2)\n" +
				"}\n" +
				"print(fib(5))\n";
		String expected =
				"(x==0):boolean\n" +
				"x:int\n" +
				"0:int\n" +
				"==:boolean\n" +
				"(x==1):boolean\n" +
				"x:int\n" +
				"1:int\n" +
				"==:boolean\n" +
				"||:boolean\n" +
				"x:int\n" +
				"fib(x-1):int\n" +
				"x:int\n" +
				"1:int\n" +
				"-:int\n" +
				"fib(x-2):int\n" +
				"x:int\n" +
				"2:int\n" +
				"-:int\n" +
				"+:int\n" +
				"fib(5):int\n" +
				"5:int\n";

				annotateTypeAndCheck(input, expected);
	}

	@Test
	public void testExprInVector() throws Exception {
		String input =
				"func sum(x:int) : int {\n" +
				"   if (x <= 0)\n" +
				"       return 0\n" +
				"   return sum(x-1)+x\n" +
				"}\n" +
				"var vec = [sum(5),1,2,3+3]\n" +
				"print(vec)\n";
		String expected =
				"x:int\n" +
				"0:int\n" +
				"<=:boolean\n" +
				"0:int\n" +
				"sum(x-1):int\n" +
				"x:int\n" +
				"1:int\n" +
				"-:int\n" +
				"x:int\n" +
				"+:int\n" +
				"[sum(5),1,2,3+3]:[]\n" +
				"sum(5):int => float\n" +
				"5:int\n" +
				"1:int => float\n" +
				"2:int => float\n" +
				"3:int\n" +
				"3:int\n" +
				"+:int => float\n" +
				"vec:[]\n";
		annotateTypeAndCheck(input, expected);
	}

	@Test
	public void testParenthesisExpr() throws Exception {
		String input =
				"var x = 1 * (2 + 3)\n" +
				"var vec = [1.0,2.0,x]\n";
		String expected =
				"1:int\n" +
				"(2+3):int\n" +
				"2:int\n" +
				"3:int\n" +
				"+:int\n" +
				"*:int\n" +
				"[1.0,2.0,x]:[]\n" +
				"1.0:float\n" +
				"2.0:float\n" +
				"x:int => float\n";
		annotateTypeAndCheck(input, expected);
	}

	@Test
	public void testVarInference() throws Exception {
		String input =
			"var x = 1\n"+
			"var y = x";
		String expected =
			"1:int\n"+
			"x:int\n";
		annotateTypeAndCheck(input, expected);
	}

	@Test
	public void testVarInferenceFromArg() throws Exception {
		String input =
			"func sum(x:string) {\n" +
			"   var y = x\n" +
			"   print(y)\n" +
			"}\n";
		String expected =
			"x:string\n"+
			"y:string\n";
		annotateTypeAndCheck(input, expected);
	}

	@Test
	public void testVarInferenceFromRetValue() throws Exception {
		String input =
			"func f() : float { }\n" +
			"var y = f()\n" +
			"print(y)\n";
		String expected =
			"f():float\n"+
			"y:float\n";
		annotateTypeAndCheck(input, expected);
	}

	@Test
	public void testPromoteArg() throws Exception {
		String input =
			"func f(x:float) { }\n" +
			"f(1)\n";
		String expected =
			"f(1):void\n"+
			"1:int => float\n";
		annotateTypeAndCheck(input, expected);
	}

	@Test
	public void testBooleanVarInferenceFromRetValue() throws Exception {
		String input =
				"func f():boolean { return (1<3) }\n" +
				"var y = f()\n"+
				"print(y)\n";
		String expecting =
				"(1<3):boolean\n"+
				"1:int\n"+
				"3:int\n"+
				"<:boolean\n"+
				"f():boolean\n"+
				"y:boolean\n";
		annotateTypeAndCheck(input, expecting);
	}

	@Test
	public void testForwardRefs() throws Exception {
		String input =
			"func f() { print(x) }\n" +
			"var x = 3\n";
		String expecting =
			"x:int\n" +
			"3:int\n";
		annotateTypeAndCheck(input, expecting);
	}

	@Test
	public void testForwardFuncRefs() throws Exception {
		String input =
			"func f() : string { g() }\n" +
			"func g() : float { f() }\n";
		String expecting =
			"g():float\n" +
			"f():string\n";
		annotateTypeAndCheck(input, expecting);
	}

	@Test
	public void testPromoteSelfAssign() throws Exception {
		String input =
				"var v = [1.0,2.0,3.0]\n" +
				"v = v + 4\n";
		String expecting = "[1.0,2.0,3.0]:[]\n" +
				"1.0:float\n" +
				"2.0:float\n" +
				"3.0:float\n" +
				"v:[]\n" +
				"4:int => []\n" +
				"+:[] => []\n";
		annotateTypeAndCheck(input, expecting);
	}

	private String getExpressionDump(String input) {
		SymbolTable symtab = new SymbolTable();
		WichErrorHandler err = new WichErrorHandler();
		ParserRuleContext tree = CompilerUtils.getAnnotatedParseTree(input, symtab, err);
		if ( tree==null ) {
			return "<invalid>";
		}
		return TypeHelper.dumpWithType(tree);
	}

	private void annotateTypeAndCheck(String input, String expected) {
		String actual = getExpressionDump(input);
		assertEquals(expected, actual);
	}
}
