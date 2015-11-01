import org.junit.Test;
import wich.Trans;
import wich.codegen.bytecode.BytecodeWriter;
import wich.codegen.bytecode.Code;
import wich.parser.WichParser;
import wich.semantics.SymbolTable;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestBytecodeGen {
	@Test
	public void testEmptyMain() throws Exception {
		String wich =
				"";
		String expecting =
				"0 strings\n" +
				"1 functions\n" +
				"0: addr=0 args=0 locals=0 type=0 4/main\n" +
				"1 instr, 1 bytes\n" +
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
				"3 instr, 9 bytes\n"+
					"ICONST 1\n"+
					"STORE 0\n"+
					"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testStringDef() throws Exception {
		String wich =
				"var s = \"hello\"\n";
		String expecting =
				"1 strings\n"+
					"0: 5/hello\n"+
				"1 functions\n" +
				"0: addr=0 args=0 locals=1 type=0 4/main\n" +
				"3 instr, 7 bytes\n"+
					"SCONST 0\n"+
					"STORE 0\n"+
					"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testVector() throws Exception {
		String wich =
				"var v = [1.00,2.00,3.00]\n";
		String excepting =
				"0 strings\n"+
				"1 functions\n"+
				"0: addr=0 args=0 locals=1 type=0 4/main\n" +
					"16 instr, 52 bytes\n"+
					"ICONST 3\n"+
					"VECTOR\n"+
					"STORE 0\n"+
					"VLOAD 0\n"+
					"ICONST 1\n"+
					"FCONST 1.0\n"+
					"STORE_INDEX\n"+
					"VLOAD 0\n"+
					"ICONST 2\n"+
					"FCONST 2.0\n"+
					"STORE_INDEX\n"+
					"VLOAD 0\n"+
					"ICONST 3\n"+
					"FCONST 3.0\n"+
					"STORE_INDEX\n"+
					"HALT\n";
		checkCodeGen(wich, excepting);
	}

	@Test
	public void testEmptyFuncs() throws Exception {
		String wich =
				"func f() {}\n" +
				"func g() {}\n";
		String expecting =
				"0 strings\n"+
				"3 functions\n"+
					"0: addr=0 args=0 locals=0 type=0 1/f\n"+
					"1: addr=1 args=0 locals=0 type=0 1/g\n"+
					"2: addr=2 args=0 locals=0 type=0 4/main\n"+
				"3 instr, 3 bytes\n" +
						"RET\n" +
						"RET\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testCallFunc() throws Exception {
		String wich =
				"func f(){g(3)}\n" +
				"func g(z:int):int{ return z }\n";
		String expecting =
				"0 strings\n"+
				"3 functions\n"+
					"0: addr=0 args=0 locals=0 type=0 1/f\n"+
					"1: addr=9 args=1 locals=0 type=1 1/g\n"+
					"2: addr=14 args=0 locals=0 type=0 4/main\n"+
				"7 instr, 15 bytes\n" +
					"ICONST 3\n" +
					"CALL 1\n" +
					"RET\n" +
					"ILOAD 0\n" +
					"RETV\n" +
					"RET\n" +
					"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testCallFuncArgs() throws Exception {
		String wich =
				"func f(q:int){var i = g(q,true)}\n" +
				"func g(z:int,b:boolean):int{ print(b) return z }\n";
		String expecting =
				"0 strings\n"+
				"3 functions\n"+
					"0: addr=0 args=1 locals=1 type=0 1/f\n"+
					"1: addr=15 args=2 locals=0 type=1 1/g\n"+
					"2: addr=24 args=0 locals=0 type=0 4/main\n"+
				"11 instr, 25 bytes\n" +
						"ILOAD 0\n" +
						"ICONST 1\n" +
						"CALL 1\n" +
						"STORE 1\n" +
						"RET\n" +
						"ILOAD 1\n" +
						"IPRINT\n" +
						"ILOAD 0\n" +
						"RETV\n" +
						"RET\n" +
						"HALT\n";
		checkCodeGen(wich, expecting);
	}

	@Test
	public void testIF() throws Exception {
		String wich =
				"var i = 3" +
				"if ( i>0 ) print (i)\n";
		String expecting =
				"0 strings\n"+
				"1 functions\n"+
				"0: addr=0 args=0 locals=1 type=0 4/main\n"+
				"9 instr, 25 bytes\n" +
						"ICONST 3\n" +
						"STORE 0\n" +
						"ILOAD 0\n" +
						"ICONST 0\n" +
						"IGT\n" +
						"BRF 7\n" +
						"ILOAD 0\n" +
						"IPRINT\n" +
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
				"1 strings\n"+
				"0: 2/hi\n"+
				"1 functions\n"+
					"0: addr=0 args=0 locals=1 type=0 4/main\n"+
				"12 instr, 32 bytes\n" +
						"ICONST 3\n" +
						"STORE 0\n" +
						"ILOAD 0\n" +
						"ICONST 0\n" +
						"IGT\n" +
						"BRF 14\n" +
						"ILOAD 0\n" +
						"IPRINT\n" +
						"BR 7\n" +
						"SCONST 0\n" +
						"SPRINT\n" +
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
				"0 strings\n"+
				"1 functions\n"+
				"0: addr=0 args=0 locals=1 type=0 4/main\n"+
				"14 instr, 40 bytes\n"+
					"ICONST 0\n"+
					"STORE 0\n"+
					"ILOAD 0\n"+
					"ICONST 10\n"+
					"ILT\n"+
					"BRF 18\n"+
					"ILOAD 0\n"+
					"ICONST 1\n"+
					"IADD\n"+
					"STORE 0\n"+
					"BR -24\n"+
					"ILOAD 0\n"+
					"IPRINT\n"+
					"HALT\n";
		checkCodeGen(wich, expecting);
	}


	@Test
	public void testRETURN() throws Exception {
		String wich =
				"return 3\n";
		String expecting =
				"0 strings\n"+
				"1 functions\n"+
				"0: addr=0 args=0 locals=0 type=0 4/main\n"+
				"3 instr, 7 bytes\n" +
					"ICONST 3\n" +
					"RETV\n" +
					"HALT\n";
		checkCodeGen(wich, expecting);
	}

	public void checkCodeGen(String wich, String expecting) throws IOException {
		Trans tool = new Trans();
		SymbolTable symtab = new SymbolTable();
		WichParser.ScriptContext tree = tool.semanticsPhase(wich, symtab);
		BytecodeWriter gen = new BytecodeWriter("foo", tool, symtab,tree);
		String result = gen.generateObjectFile();
		result = result.replaceAll("\t", "");
		assertEquals(expecting, result);
	}
}
