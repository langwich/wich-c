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
		String lava =
			"";
		String expecting =
			"1 instr, 1 bytes\n" +
			"HALT\n";
		checkCodeGen(lava, expecting);
	}

	@Test
	public void testJustDecls() throws Exception {
		String lava =
			"int i;\n" +
			"boolean b;\n";
		String expecting =
			"1 instr, 1 bytes\n" +
			"HALT\n";
		checkCodeGen(lava, expecting);
	}

	@Test
	public void testDeclsWithInit() throws Exception {
		String lava =
			"int i = 3;\n" +
			"boolean b = true;\n";
		String expecting =
			"5 instr, 17 bytes\n" +
			"ICONST 3\n" +
			"STORE_GLOBAL 0\n" +
			"ICONST 1\n" +
			"STORE_GLOBAL 1\n" +
			"HALT\n";
		checkCodeGen(lava, expecting);
	}

	@Test
	public void testDeclsWithManualInit() throws Exception {
		String lava =
			"int i;\n" +
			"i = 3;\n" +	// flips us to main() code block
			"boolean b;\n" +
			"b = true;\n";
		String expecting =
			"5 instr, 17 bytes\n" +
			"ICONST 3\n" +
			"STORE_GLOBAL 0\n" +
			"ICONST 1\n" +
			"STORE 0\n" +
			"HALT\n";
		checkCodeGen(lava, expecting);
	}

	@Test
	public void testEmptyFuncs() throws Exception {
		String lava =
			"void f() { int i; }\n" +
			"int g(int z) { float z; }\n";
		String expecting =
			"3 instr, 3 bytes\n" +
			"RET\n" +
			"RET\n" +
			"HALT\n";
		checkCodeGen(lava, expecting);
	}

	@Test
	public void testCallFunc() throws Exception {
		String lava =
			"void f() { g(3); }\n" +
			"int g(int z) { return z; }\n";
		String expecting =
			"7 instr, 15 bytes\n" +
			"ICONST 3\n" +
			"CALL 1\n" +
			"RET\n" +
			"ILOAD 0\n" +
			"RETV\n" +
			"RET\n" +
			"HALT\n";
		checkCodeGen(lava, expecting);
	}

	@Test
	public void testCallFuncArgs() throws Exception {
		String lava =
			"void f(int q) { int i = g(q,true); }\n" +
			"int g(int z, boolean b) { print b; return z; }\n";
		String expecting =
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
		checkCodeGen(lava, expecting);
	}

	@Test
	public void testStructStore() throws Exception {
		String lava =
			"struct User {\n" +
			"  int x;\n" +
			"  boolean b;\n" +
			"}\n" +
			"User u = new User();\n" +
			"u.x = 3;\n" +
			"u.b = true;\n";
		String expecting =
			"9 instr, 29 bytes\n" +
			"NEW 0\n" +
			"STORE_GLOBAL 0\n" +
			"LOAD_GLOBAL 0\n" +
			"ICONST 3\n" +
			"STORE_FIELD 0\n" +
			"LOAD_GLOBAL 0\n" +
			"ICONST 1\n" +
			"STORE_FIELD 1\n" +
			"HALT\n";
		checkCodeGen(lava, expecting);
	}

	@Test
	public void testStructRef() throws Exception {
		String lava =
			"struct User {\n" +
			"  int x;\n" +
			"  boolean b;\n" +
			"}\n" +
			"User u = new User();\n" +
			"print u.x;\n";
		String expecting =
			"6 instr, 14 bytes\n" +
			"NEW 0\n" +
			"STORE_GLOBAL 0\n" +
			"LOAD_GLOBAL 0\n" +
			"LOAD_FIELD 0\n" +
			"IPRINT\n" +
			"HALT\n";
		checkCodeGen(lava, expecting);
	}

	@Test
	public void testIF() throws Exception {
		String lava =
			"int i = 3;" +
			"if ( i>0 ) print i;\n";
		String expecting =
			"9 instr, 25 bytes\n" +
			"ICONST 3\n" +
			"STORE_GLOBAL 0\n" +
			"LOAD_GLOBAL 0\n" +
			"ICONST 0\n" +
			"IGT\n" +
			"BRF 7\n" +
			"LOAD_GLOBAL 0\n" +
			"IPRINT\n" +
			"HALT\n";
		checkCodeGen(lava, expecting);
	}

	@Test
	public void testIF_ELSE() throws Exception {
		String lava =
			"int i = 3;" +
			"if ( i>0 ) print i;\n" +
			"else print \"hi\";";
		String expecting =
			"12 instr, 32 bytes\n" +
			"ICONST 3\n" +
			"STORE_GLOBAL 0\n" +
			"LOAD_GLOBAL 0\n" +
			"ICONST 0\n" +
			"IGT\n" +
			"BRF 14\n" +
			"LOAD_GLOBAL 0\n" +
			"IPRINT\n" +
			"BR 7\n" +
			"SCONST 0\n" +
			"PPRINT\n" +
			"HALT\n";
		checkCodeGen(lava, expecting);
	}

	@Test
	public void testWHILE() throws Exception {
		String lava =
			"int i = 0;" +
			"while ( i<10 ) { print i; i = i + 1; }\n";
		String expecting =
			"14 instr, 40 bytes\n" +
			"ICONST 0\n" +
			"STORE_GLOBAL 0\n" +
			"LOAD_GLOBAL 0\n" +
			"ICONST 10\n" +
			"ILT\n" +
			"BRF 22\n" +
			"LOAD_GLOBAL 0\n" +
			"IPRINT\n" +
			"LOAD_GLOBAL 0\n" +
			"ICONST 1\n" +
			"IADD\n" +
			"STORE_GLOBAL 0\n" +
			"BR -28\n" +
			"HALT\n";
		checkCodeGen(lava, expecting);
	}

	@Test
	public void testFOR() throws Exception {
		String lava =
			"for (int i = 0; i<10; i = i + 1) { print i; }\n";
		String expecting =
			"14 instr, 40 bytes\n" +
			"ICONST 0\n" +
			"STORE 0\n" +
			"ILOAD 0\n" +
			"ICONST 10\n" +
			"ILT\n" +
			"BRF 22\n" +
			"ILOAD 0\n" +
			"IPRINT\n" +
			"ILOAD 0\n" +
			"ICONST 1\n" +
			"IADD\n" +
			"STORE 0\n" +
			"BR -28\n" +
			"HALT\n";
		checkCodeGen(lava, expecting);
	}

	@Test
	public void testRETURN() throws Exception {
		String lava =
			"return 3;\n";
		String expecting =
			"3 instr, 7 bytes\n" +
			"ICONST 3\n" +
			"RETV\n" +
			"HALT\n";
		checkCodeGen(lava, expecting);
	}

	public void checkCodeGen(String lava, String expecting) throws IOException {
		Trans tool = new Trans();
		WichParser.ScriptContext tree = tool.parseString(lava);
		SymbolTable symtab = new SymbolTable("<test>", tree);
		tool.semanticsPhase(symtab);
		BytecodeWriter gen = new BytecodeWriter("foo", tool, symtab);
		Code code = gen.genBytecode();
		String buf = String.format("%d instr, %d bytes\n", code.instructions().size(), code.sizeBytes());
		buf += gen.serializedCode(code);
		String result = buf;
		result = result.replaceAll("\t", "");
		assertEquals(expecting, result);
	}
}
