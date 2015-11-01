package wich.codegen.bytecode;

import wich.Trans;
import wich.codegen.CompilerUtils;
import wich.parser.WichParser;
import wich.semantics.SymbolTable;
import wich.semantics.symbols.WFunctionSymbol;
import wich.semantics.symbols.WVariableSymbol;

import java.util.Map;

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

	/** Return a string representation of the object file. */
	public String generateObjectFile() {
		Code code = genBytecode();

		StringBuilder buf = new StringBuilder();
		buf.append(String.format("%d strings\n", symtab.strings.size()));
		for (String s : symtab.strings.keySet()) {
			Integer i = symtab.strings.get(s);
			s = CompilerUtils.stripFirstLast(s);
			buf.append(String.format("\t%d: %d/%s\n", i, s.length(), s));
		}
		buf.append(String.format("%d functions\n", symtab.getfunctions().size()));
		for (String s : symtab.getfunctions().keySet()) {
			WFunctionSymbol f = symtab.getfunctions().get(s);
			int numLocalsAndArgs = f.nlocals();
			int numArgs = f.nargs();
			buf.append(String.format("\t%d: addr=%d args=%d locals=%d type=%d %d/%s\n",
					symtab.computerFuncIndex(f.getInsertionOrderNumber()), f.address, numArgs, numLocalsAndArgs,
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
//				System.out.println(ip+": "+I);
				I.address = ip;
				ip += I.size;
			}
			Instr firstInstr = body.instructions().get(0);
			fsym.address = firstInstr.address;
		}
	}
}
