import org.junit.Test;
import wich.Trans;
import wich.codegen.bytecode.BytecodeWriter;
import wich.parser.WichParser;
import wich.semantics.SymbolTable;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestBytecodeGen {

	@Test
	public void testEmptyMain() throws Exception {
		String wich =
				"";
		String expecting =
				"0 strings\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=0 type=0 4/main\n" +
						"3 instr, 3 bytes\n" +
						"GC_START\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testVarDef() throws Exception {
		String wich =
				"var i = 1\n";
		String expecting =
				"0 strings\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=1 type=0 4/main\n" +
						"5 instr, 11 bytes\n" +
						"GC_START\n" +
						"ICONST 1\n" +
						"STORE 0\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testStringDef() throws Exception {
		String wich =
				"var s = \"hello\"\n";
		String expecting =
				"1 strings\n" +
						"0: 5/hello\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=1 type=0 4/main\n" +
						"6 instr, 10 bytes\n" +
						"GC_START\n" +
						"SCONST 0\n" +
						"STORE 0\n" +
						"SROOT\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testVector() throws Exception {
		String wich =
				"var v = [1.0,2.0,3.0]\n";
		String excepting =
				"0 strings\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=1 type=0 4/main\n" +
						"10 instr, 28 bytes\n" +
						"GC_START\n" +
						"FCONST 1.0\n" +
						"FCONST 2.0\n" +
						"FCONST 3.0\n" +
						"ICONST 3\n" +
						"VECTOR\n" +
						"STORE 0\n" +
						"VROOT\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, excepting);
	}

	@Test
	public void testVectorPromotion() throws Exception {
		String wich =
				"var v = [1,2,3]\n";
		String excepting =
				"0 strings\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=1 type=0 4/main\n" +
						"13 instr, 31 bytes\n" +
						"GC_START\n" +
						"ICONST 1\n" +
						"I2F\n" +
						"ICONST 2\n" +
						"I2F\n" +
						"ICONST 3\n" +
						"I2F\n" +
						"ICONST 3\n" +
						"VECTOR\n" +
						"STORE 0\n" +
						"VROOT\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, excepting);
	}
	@Test
	public void testEmptyFuncs() throws Exception {
		String wich =
				"func f() {}\n" +
				"func g() {}\n";
		String expecting =
				"0 strings\n" +
						"3 functions\n" +
						"0: addr=0 args=0 locals=0 type=0 1/f\n" +
						"1: addr=4 args=0 locals=0 type=0 1/g\n" +
						"2: addr=8 args=0 locals=0 type=0 4/main\n" +
						"11 instr, 11 bytes\n" +
						"GC_START\n" +
						"NOP\n" +
						"RET\n" +
						"GC_END\n" +
						"GC_START\n" +
						"NOP\n" +
						"RET\n" +
						"GC_END\n" +
						"GC_START\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testCallFunc() throws Exception {
		String wich =
				"func f(){g(3)}\n" +
				"func g(z:int):int{ return z }\n";
		String expecting =
				"0 strings\n" +
						"3 functions\n" +
						"0: addr=0 args=0 locals=0 type=0 1/f\n" +
						"1: addr=12 args=1 locals=0 type=1 1/g\n" +
						"2: addr=21 args=0 locals=0 type=0 4/main\n" +
						"16 instr, 24 bytes\n" +
						"GC_START\n" +
						"ICONST 3\n" +
						"CALL 1\n" +
						"POP\n" +
						"RET\n" +
						"GC_END\n" +
						"GC_START\n" +
						"ILOAD 0\n" +
						"GC_END\n" +
						"RET\n" +
						"PUSH_DFLT_RETV\n" +
						"RET\n" +
						"GC_END\n" +
						"GC_START\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testCallFuncArgs() throws Exception {
		String wich =
				"func f(q:int){var i = g(q,true)}\n" +
				"func g(z:int,b:boolean):int{ print(b) return z }\n";
		String expecting =
				"0 strings\n" +
						"3 functions\n" +
						"0: addr=0 args=1 locals=1 type=0 1/f\n" +
						"1: addr=17 args=2 locals=0 type=1 1/g\n" +
						"2: addr=30 args=0 locals=0 type=0 4/main\n" +
						"19 instr, 33 bytes\n" +
						"GC_START\n" +
						"ILOAD 0\n" +
						"ICONST 1\n" +
						"CALL 1\n" +
						"STORE 1\n" +
						"RET\n" +
						"GC_END\n" +
						"GC_START\n" +
						"ILOAD 1\n" +
						"BPRINT\n" +
						"ILOAD 0\n" +
						"GC_END\n" +
						"RET\n" +
						"PUSH_DFLT_RETV\n" +
						"RET\n" +
						"GC_END\n" +
						"GC_START\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testIF() throws Exception {
		String wich =
				"var i = 3" +
				"if ( i>0 ) print (i)\n";
		String expecting =
				"0 strings\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=1 type=0 4/main\n" +
						"11 instr, 27 bytes\n" +
						"GC_START\n" +
						"ICONST 3\n" +
						"STORE 0\n" +
						"ILOAD 0\n" +
						"ICONST 0\n" +
						"IGT\n" +
						"BRF 7\n" +
						"ILOAD 0\n" +
						"IPRINT\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testIF_ELSE() throws Exception {
		String wich =
				"var i = 3" +
				"if ( i>0 ) print (i)\n" +
				"else print (\"hi\")";
		String expecting =
				"1 strings\n" +
						"0: 2/hi\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=1 type=0 4/main\n" +
						"14 instr, 34 bytes\n" +
						"GC_START\n" +
						"ICONST 3\n" +
						"STORE 0\n" +
						"ILOAD 0\n" +
						"ICONST 0\n" +
						"IGT\n" +
						"BRF 10\n" +
						"ILOAD 0\n" +
						"IPRINT\n" +
						"BR 7\n" +
						"SCONST 0\n" +
						"SPRINT\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testWHILE() throws Exception {
		String wich =
				"var i = 0" +
				"while ( i<10 ) {i = i + 1 }\n"+
				"print(i)";
		String expecting =
				"0 strings\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=1 type=0 4/main\n" +
						"16 instr, 42 bytes\n" +
						"GC_START\n" +
						"ICONST 0\n" +
						"STORE 0\n" +
						"ILOAD 0\n" +
						"ICONST 10\n" +
						"ILT\n" +
						"BRF 18\n" +
						"ILOAD 0\n" +
						"ICONST 1\n" +
						"IADD\n" +
						"STORE 0\n" +
						"BR -24\n" +
						"ILOAD 0\n" +
						"IPRINT\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}


	@Test
	public void testRETURN() throws Exception {
		String wich =
				"return 3\n";
		String expecting =
				"0 strings\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=0 type=0 4/main\n" +
						"6 instr, 10 bytes\n" +
						"GC_START\n" +
						"ICONST 3\n" +
						"GC_END\n" +
						"RET\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testNestedBlock() throws Exception {
		String wich =
				"var x = 3\n"+
				"{var y = 1 print (x+y)}\n";
		String expecting =
				"0 strings\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=2 type=0 4/main\n" +
						"11 instr, 27 bytes\n" +
						"GC_START\n" +
						"ICONST 3\n" +
						"STORE 0\n" +
						"ICONST 1\n" +
						"STORE 1\n" +
						"ILOAD 0\n" +
						"ILOAD 1\n" +
						"IADD\n" +
						"IPRINT\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testNestedBlock2() throws Exception {
		String wich =
				"func f(x:int){{var y = 1 print(x+y)}}\n"+
				"f(1)\n";
		String expecting =
				"0 strings\n" +
						"2 functions\n" +
						"0: addr=0 args=1 locals=1 type=0 1/f\n" +
						"1: addr=19 args=0 locals=0 type=0 4/main\n" +
						"14 instr, 30 bytes\n" +
						"GC_START\n" +
						"ICONST 1\n" +
						"STORE 1\n" +
						"ILOAD 0\n" +
						"ILOAD 1\n" +
						"IADD\n" +
						"IPRINT\n" +
						"RET\n" +
						"GC_END\n" +
						"GC_START\n" +
						"ICONST 1\n" +
						"CALL 0\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testElementAssign() throws Exception{
		String wich =
				"var v = [1.0,2.0,3.0]\n"+
				"v[1] = 4.0\n";
		String expecting =
				"0 strings\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=1 type=0 4/main\n" +
						"14 instr, 42 bytes\n" +
						"GC_START\n" +
						"FCONST 1.0\n" +
						"FCONST 2.0\n" +
						"FCONST 3.0\n" +
						"ICONST 3\n" +
						"VECTOR\n" +
						"STORE 0\n" +
						"VROOT\n" +
						"VLOAD 0\n" +
						"ICONST 1\n" +
						"FCONST 4.0\n" +
						"STORE_INDEX\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich,expecting);
	}

	@Test
	public void testElementAssignPromotion() throws Exception{
		String wich =
				"var v = [1.0,2.0,3.0]\n"+
						"v[1] = 4\n";
		String expecting =
				"0 strings\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=1 type=0 4/main\n" +
						"15 instr, 43 bytes\n" +
						"GC_START\n" +
						"FCONST 1.0\n" +
						"FCONST 2.0\n" +
						"FCONST 3.0\n" +
						"ICONST 3\n" +
						"VECTOR\n" +
						"STORE 0\n" +
						"VROOT\n" +
						"VLOAD 0\n" +
						"ICONST 1\n" +
						"ICONST 4\n" +
						"I2F\n" +
						"STORE_INDEX\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich,expecting);
	}

	@Test
	public void testOpI2FPromotion() throws Exception{
		String wich =
				"var x = 1\n"+
				"var y = 3.14 + x\n";
		String expecting =
				"0 strings\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=2 type=0 4/main\n" +
						"10 instr, 24 bytes\n" +
						"GC_START\n" +
						"ICONST 1\n" +
						"STORE 0\n" +
						"FCONST 3.14\n" +
						"ILOAD 0\n" +
						"I2F\n" +
						"FADD\n" +
						"STORE 1\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich,expecting);
	}

	@Test
	public void testVOpI() throws Exception{
		String wich =
				"var v = [1.0,2.0,3.0]\n"+
				"v = v + 4\n";

		String expecting =
				"0 strings\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=1 type=0 4/main\n" +
						"14 instr, 40 bytes\n" +
						"GC_START\n" +
						"FCONST 1.0\n" +
						"FCONST 2.0\n" +
						"FCONST 3.0\n" +
						"ICONST 3\n" +
						"VECTOR\n" +
						"STORE 0\n" +
						"VROOT\n" +
						"VLOAD 0\n" +
						"ICONST 4\n" +
						"VADDI\n" +
						"STORE 0\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich,expecting);
	}

	@Test
	public void testVOpIReverse() throws Exception{
		String wich =
				"var v = [1.0,2.0,3.0]\n"+
						"v = 4 + v\n";

		String expecting =
				"0 strings\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=1 type=0 4/main\n" +
						"14 instr, 40 bytes\n" +
						"GC_START\n" +
						"FCONST 1.0\n" +
						"FCONST 2.0\n" +
						"FCONST 3.0\n" +
						"ICONST 3\n" +
						"VECTOR\n" +
						"STORE 0\n" +
						"VROOT\n" +
						"VLOAD 0\n" +
						"ICONST 4\n" +
						"VADDI\n" +
						"STORE 0\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich,expecting);
	}

	@Test
	public void testVOpF() throws Exception{
		String wich =
				"var v = [1.0,2.0,3.0]\n"+
						"v = v + 3.14\n";

		String expecting =
				"0 strings\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=1 type=0 4/main\n" +
						"14 instr, 40 bytes\n" +
						"GC_START\n" +
						"FCONST 1.0\n" +
						"FCONST 2.0\n" +
						"FCONST 3.0\n" +
						"ICONST 3\n" +
						"VECTOR\n" +
						"STORE 0\n" +
						"VROOT\n" +
						"VLOAD 0\n" +
						"FCONST 3.14\n" +
						"VADDF\n" +
						"STORE 0\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich,expecting);
	}

	@Test
	public void testVOpFReverse() throws Exception{
		String wich =
				"var v = [1.0,2.0,3.0]\n"+
						"v = 3.14 + v\n";

		String expecting =
				"0 strings\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=1 type=0 4/main\n" +
						"14 instr, 40 bytes\n" +
						"GC_START\n" +
						"FCONST 1.0\n" +
						"FCONST 2.0\n" +
						"FCONST 3.0\n" +
						"ICONST 3\n" +
						"VECTOR\n" +
						"STORE 0\n" +
						"VROOT\n" +
						"VLOAD 0\n" +
						"FCONST 3.14\n" +
						"VADDF\n" +
						"STORE 0\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich,expecting);
	}

	@Test
	public void testCompareNumeric() throws Exception {
		String wich =
				"var i = 3\n" +
						"if (i == 3 ) print (i)\n";
		String expecting =
				"0 strings\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=1 type=0 4/main\n" +
						"11 instr, 27 bytes\n" +
						"GC_START\n" +
						"ICONST 3\n" +
						"STORE 0\n" +
						"ILOAD 0\n" +
						"ICONST 3\n" +
						"IEQ\n" +
						"BRF 7\n" +
						"ILOAD 0\n" +
						"IPRINT\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}


	@Test
	public void testCompareString() throws Exception {
		String wich =
				"var s = \"abc\" \n" +
						"if (s == \"abc\" ) print (s)\n";
		String expecting =
				"1 strings\n" +
						"0: 3/abc\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=1 type=0 4/main\n" +
						"12 instr, 24 bytes\n" +
						"GC_START\n" +
						"SCONST 0\n" +
						"STORE 0\n" +
						"SROOT\n" +
						"SLOAD 0\n" +
						"SCONST 0\n" +
						"SEQ\n" +
						"BRF 7\n" +
						"SLOAD 0\n" +
						"SPRINT\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testStrAddStr() throws Exception {
		String wich =
				"var s1 = \"abc\" \n" +
						"var s2 = s1 + \"xyz\" \n";
		String expecting =
				"2 strings\n" +
						"0: 3/abc\n" +
						"1: 3/xyz\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=2 type=0 4/main\n" +
						"11 instr, 21 bytes\n" +
						"GC_START\n" +
						"SCONST 0\n" +
						"STORE 0\n" +
						"SROOT\n" +
						"SLOAD 0\n" +
						"SCONST 1\n" +
						"SADD\n" +
						"STORE 1\n" +
						"SROOT\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testStrAddInt() throws Exception {
		String wich =
				"var s1 = \"abc\" \n" +
						"var s2 = s1 + 100 \n";
		String expecting =
				"1 strings\n" +
						"0: 3/abc\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=2 type=0 4/main\n" +
						"12 instr, 24 bytes\n" +
						"GC_START\n" +
						"SCONST 0\n" +
						"STORE 0\n" +
						"SROOT\n" +
						"SLOAD 0\n" +
						"ICONST 100\n" +
						"I2S\n" +
						"SADD\n" +
						"STORE 1\n" +
						"SROOT\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testStrAddFloat() throws Exception {
		String wich =
				"var s1 = \"abc\" \n" +
						"var s2 = s1 + 3.14 \n";
		String expecting =
				"1 strings\n" +
						"0: 3/abc\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=2 type=0 4/main\n" +
						"12 instr, 24 bytes\n" +
						"GC_START\n" +
						"SCONST 0\n" +
						"STORE 0\n" +
						"SROOT\n" +
						"SLOAD 0\n" +
						"FCONST 3.14\n" +
						"F2S\n" +
						"SADD\n" +
						"STORE 1\n" +
						"SROOT\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testStrAddVector() throws Exception {
		String wich =
				"var s1 = \"abc\" \n" +
						"var s2 = s1 + [1,2,3] \n";
		String expecting =
				"1 strings\n" +
						"0: 3/abc\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=2 type=0 4/main\n" +
						"19 instr, 43 bytes\n" +
						"GC_START\n" +
						"SCONST 0\n" +
						"STORE 0\n" +
						"SROOT\n" +
						"SLOAD 0\n" +
						"ICONST 1\n" +
						"I2F\n" +
						"ICONST 2\n" +
						"I2F\n" +
						"ICONST 3\n" +
						"I2F\n" +
						"ICONST 3\n" +
						"VECTOR\n" +
						"V2S\n" +
						"SADD\n" +
						"STORE 1\n" +
						"SROOT\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	public void testPopReturnVal() throws Exception {
		String wich =
				"func sq(q:int): int {return q*q}\n"+
						"sq(10)\n";

		String expecting =
				"0 strings\n"+
						"2 functions\n"+
						"0: addr=0 args=1 locals=0 type=1 2/sq\n"+
						"1: addr=9 args=0 locals=0 type=0 4/main\n"+
						"9 instr, 19 bytes\n" +
						"ILOAD 0\n" +
						"ILOAD 0\n" +
						"IMUL\n" +
						"RET\n" +
						"RET\n" +
						"ICONST 10\n" +
						"CALL 0\n" +
						"POP\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testNop() throws Exception {
		String Wich = "var i = 3" +
				"if ( i>0 ) {}\n" +
				"else print (\"hi\")";
		String expecting ="1 strings\n" +
				"0: 2/hi\n" +
				"1 functions\n" +
				"0: addr=0 args=0 locals=1 type=0 4/main\n" +
				"13 instr, 31 bytes\n" +
				"GC_START\n" +
				"ICONST 3\n" +
				"STORE 0\n" +
				"ILOAD 0\n" +
				"ICONST 0\n" +
				"IGT\n" +
				"BRF 7\n" +
				"NOP\n" +
				"BR 7\n" +
				"SCONST 0\n" +
				"SPRINT\n" +
				"GC_END\n" +
				"HALT\n";
		checkCodeGen(Wich, expecting);
	}

	@Test
	public void testFuncWithReturnError() throws Exception {
		String Wich = "func f(x:int):[] { if(x<0) { return x+[0] }\n" +
				"else{\n" +
				"\tx = x + [1]\n" +
				"\t}\n" +
				"}\n"+
				"print (f(3))\n";
		String expecting =
				"0 strings\n" +
						"2 functions\n" +
						"0: addr=0 args=1 locals=0 type=5 1/f\n" +
						"1: addr=56 args=0 locals=0 type=0 4/main\n" +
						"30 instr, 68 bytes\n" +
						"GC_START\n" +
						"ILOAD 0\n" +
						"ICONST 0\n" +
						"ILT\n" +
						"BRF 24\n" +
						"ICONST 0\n" +
						"I2F\n" +
						"ICONST 1\n" +
						"VECTOR\n" +
						"ILOAD 0\n" +
						"VADDI\n" +
						"GC_END\n" +
						"RET\n" +
						"BR 22\n" +
						"ICONST 1\n" +
						"I2F\n" +
						"ICONST 1\n" +
						"VECTOR\n" +
						"ILOAD 0\n" +
						"VADDI\n" +
						"STORE 0\n" +
						"PUSH_DFLT_RETV\n" +
						"RET\n" +
						"GC_END\n" +
						"GC_START\n" +
						"ICONST 3\n" +
						"CALL 0\n" +
						"VPRINT\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(Wich, expecting);
	}

	@Test
	public void testLen() throws Exception {
		String Wich =
				"var a = \"hello\"\n" +
				"var b = len([1,2,3])\n" +
				"print (len(a)+len(\"world\")+b)\n";
		String expecting =
				"2 strings\n" +
						"0: 5/hello\n" +
						"1: 5/world\n" +
						"1 functions\n" +
						"0: addr=0 args=0 locals=2 type=0 4/main\n" +
						"24 instr, 52 bytes\n" +
						"GC_START\n" +
						"SCONST 0\n" +
						"STORE 0\n" +
						"SROOT\n" +
						"ICONST 1\n" +
						"I2F\n" +
						"ICONST 2\n" +
						"I2F\n" +
						"ICONST 3\n" +
						"I2F\n" +
						"ICONST 3\n" +
						"VECTOR\n" +
						"VLEN\n" +
						"STORE 1\n" +
						"SLOAD 0\n" +
						"SLEN\n" +
						"SCONST 1\n" +
						"SLEN\n" +
						"IADD\n" +
						"ILOAD 1\n" +
						"IADD\n" +
						"IPRINT\n" +
						"GC_END\n" +
						"HALT\n";
		checkCodeGen(Wich,expecting);
	}
	@Test
	public void bubbleSort() throws Exception {
		String Wich =
				"func bubbleSort(vec:[]):[] {\n" +
					"var length = len(vec)\n" +
					"var v = vec\n" +
					"var i = 0\n" +
					"var j = 0\n" +
					"while(i< length){\n" +
						"while(j<((length - i))){\n" +
							"if (v[j] > v[j+1]){\n" +
								"var swap = v[j]\n" +
								"v[j] = v[j+1]\n" +
								"v[j+1] = swap\n" +
							"}\n" +
						"j = j+1\n" +
				"}\n" +
				"i = i+1\n" +
				"}\n" +
				"return v\n" +
				"}\n" +
				"\n" +
				"var x = [1,4,2,3]\n" +
				"print(bubbleSort(x))\n";
		String expecting = "0 strings\n" +
				"2 functions\n" +
				"0: addr=0 args=1 locals=4 type=5 10/bubbleSort\n" +
				"1: addr=164 args=0 locals=1 type=0 4/main\n" +
				"85 instr, 209 bytes\n" +
				"GC_START\n" +
				"VLOAD 0\n" +
				"VLEN\n" +
				"STORE 1\n" +
				"VLOAD 0\n" +
				"COPY_VECTOR\n" +
				"STORE 2\n" +
				"VROOT\n" +
				"ICONST 0\n" +
				"STORE 3\n" +
				"ICONST 0\n" +
				"STORE 4\n" +
				"ILOAD 3\n" +
				"ILOAD 1\n" +
				"ILT\n" +
				"BRF 117\n" +
				"ILOAD 4\n" +
				"ILOAD 1\n" +
				"ILOAD 3\n" +
				"ISUB\n" +
				"ILT\n" +
				"BRF 88\n" +
				"VLOAD 2\n" +
				"ILOAD 4\n" +
				"VLOAD_INDEX\n" +
				"VLOAD 2\n" +
				"ILOAD 4\n" +
				"ICONST 1\n" +
				"IADD\n" +
				"VLOAD_INDEX\n" +
				"FGT\n" +
				"BRF 49\n" +
				"VLOAD 2\n" +
				"ILOAD 4\n" +
				"VLOAD_INDEX\n" +
				"STORE 5\n" +
				"VLOAD 2\n" +
				"ILOAD 4\n" +
				"VLOAD 2\n" +
				"ILOAD 4\n" +
				"ICONST 1\n" +
				"IADD\n" +
				"VLOAD_INDEX\n" +
				"STORE_INDEX\n" +
				"VLOAD 2\n" +
				"ILOAD 4\n" +
				"ICONST 1\n" +
				"IADD\n" +
				"FLOAD 5\n" +
				"STORE_INDEX\n" +
				"ILOAD 4\n" +
				"ICONST 1\n" +
				"IADD\n" +
				"STORE 4\n" +
				"BR -96\n" +
				"ILOAD 3\n" +
				"ICONST 1\n" +
				"IADD\n" +
				"STORE 3\n" +
				"BR -121\n" +
				"VLOAD 2\n" +
				"GC_END\n" +
				"RET\n" +
				"PUSH_DFLT_RETV\n" +
				"RET\n" +
				"GC_END\n" +
				"GC_START\n" +
				"ICONST 1\n" +
				"I2F\n" +
				"ICONST 4\n" +
				"I2F\n" +
				"ICONST 2\n" +
				"I2F\n" +
				"ICONST 3\n" +
				"I2F\n" +
				"ICONST 4\n" +
				"VECTOR\n" +
				"STORE 0\n" +
				"VROOT\n" +
				"VLOAD 0\n" +
				"COPY_VECTOR\n" +
				"CALL 0\n" +
				"VPRINT\n" +
				"GC_END\n" +
				"HALT\n";
		checkCodeGen(Wich,expecting);
	}

	@Test
	public void test_float_div() throws Exception {
		String Wich = "var x = 1.0\n" +
				"var y = 2.0\n" +
				"print (y/x)\n";
		String expecting = "0 strings\n" +
				"1 functions\n" +
				"0: addr=0 args=0 locals=2 type=0 4/main\n" +
				"11 instr, 27 bytes\n" +
				"GC_START\n" +
				"FCONST 1.0\n" +
				"STORE 0\n" +
				"FCONST 2.0\n" +
				"STORE 1\n" +
				"FLOAD 1\n" +
				"FLOAD 0\n" +
				"FDIV\n" +
				"FPRINT\n" +
				"GC_END\n" +
				"HALT\n";
		checkCodeGen(Wich,expecting);
	}

	public void checkCodeGen(String wich, String expecting) throws IOException {
		Trans tool = new Trans();
		SymbolTable symtab = new SymbolTable();
		WichParser.ScriptContext tree = tool.semanticsPhase(wich, symtab);
		assertFalse(tree==null);
		BytecodeWriter gen = new BytecodeWriter("foo", symtab, tree);
		String result = gen.genObjectFile();
		result = result.replaceAll("\t", "");
		assertEquals(expecting, result);
	}
}
