package wich.codegen.bytecode;

import wich.Trans;
import wich.codegen.CompilerUtils;
import wich.parser.WichParser;
import wich.semantics.SymbolTable;
import wich.semantics.symbols.WFunctionSymbol;
import wich.semantics.symbols.WVariableSymbol;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/** Generate a file containing bytecode and symbol table information
 *  so that an interpreter/VM can execute the code.  For ease
 *  interoperability and debugging ease, generate text not binary file.
 *
 *  foo.lava source text file generates foo.olava object text file.
 */
public class BytecodeWriter {
	public Trans tool;
	public SymbolTable symtab;
	public String fileName;
	public WichParser.ScriptContext tree;

	public BytecodeWriter(String fileName, Trans tool, SymbolTable symtab,WichParser.ScriptContext tree) {
		this.fileName = fileName;
		this.tool = tool;
		this.symtab = symtab;
		this.tree = tree;
	}


	public static void writeFile(String path, String output, Charset encoding) throws IOException {
		Files.write(Paths.get(path), output.getBytes(encoding));
	}
	/** Return a string representation of the object file. */
	public String generateObjectFile() {
		Code code = genBytecode();

		StringBuilder buf = new StringBuilder();
		buf.append(String.format("%d strings\n", symtab.strings.size()));
		LinkedHashMap sortedMap = (LinkedHashMap<String, Integer>)symtab.sortHashMapByValues(symtab.strings);
		for (Object s : sortedMap.keySet()) {
			Integer i = (Integer) sortedMap.get(s);
			String literal = CompilerUtils.stripFirstLast((String)s);
			buf.append(String.format("\t%d: %d/%s\n", i, literal.length(), literal));
		}
		buf.append(String.format("%d functions\n", symtab.getfunctions().size()));
		List<Integer> insertOrder = new ArrayList<>();
		HashMap<Integer, String> indexMapName = new HashMap<>();
		for (String s : symtab.getfunctions().keySet()) {
			int index = symtab.computerFuncIndex(s);
			insertOrder.add(index);
			indexMapName.put(index, s);
		}
		Collections.sort(insertOrder);
		for (Integer i :insertOrder) {
			String s = indexMapName.get(i);
			WFunctionSymbol f = symtab.getfunctions().get(s);
			int numLocalsAndArgs = f.nlocals();
			int numArgs = f.nargs();
			buf.append(String.format("\t%d: addr=%d args=%d locals=%d type=%d %d/%s\n",
					symtab.computerFuncIndex(f.getName()), f.address, numArgs, numLocalsAndArgs,
					f.getType().getVMTypeIndex(), s.length(), s));
		}

		StringBuilder codeS = serializedCode(code);
		buf.append(String.format("%d instr, %d bytes\n", code.instructions().size(), code.sizeBytes()));
		buf.append(codeS);
		return buf.toString();
	}

	public Code genBytecode() {
		BytecodeGenerator bgen = new BytecodeGenerator(symtab);
		bgen.visit(tree);
		computeCodeAddresses(bgen.functionBodies);
		Code all = Code.None;
		for (Code code : bgen.functionBodies.values()) {
			all = all.join(code);
		}
		return all;
	}

	public StringBuilder serializedCode(Code code) {
		StringBuilder codeS = new StringBuilder();
		for (Instr I : code.instructions()) {
			codeS.append("\t");
			codeS.append(I);
			codeS.append('\n');
		}
		return codeS;
	}

	public void computeCodeAddresses(Map<String, Code> functionBodies) {
		int ip = 0; // compute addresses for each instruction across functions in order
		for (String fname : functionBodies.keySet()) {
			WFunctionSymbol fsym = symtab.getfunctions().get(fname);
			Code body = functionBodies.get(fname);
			for (Instr I : body.instructions()) {
				I.address = ip;
				ip += I.size;
			}
			Instr firstInstr = body.instructions().get(0);
			fsym.address = firstInstr.address;
		}
	}
}
