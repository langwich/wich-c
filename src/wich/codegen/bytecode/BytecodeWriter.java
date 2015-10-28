package wich.codegen.bytecode;

import org.antlr.symtab.FunctionSymbol;
import wich.Trans;
import wich.codegen.CompilerUtils;
import wich.semantics.SymbolTable;

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

	public BytecodeWriter(String fileName, Trans tool, SymbolTable symtab) {
		this.fileName = fileName;
		this.tool = tool;
		this.symtab = symtab;
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
		buf.append(String.format("%d functions\n", symtab.functions.size()));
		for (String s : symtab.functions.keySet()) {
			FunctionSymbol f = symtab.functions.get(s);
			int numLocalsAndArgs = f.getAllSymbols().size();
			int numArgs = f.getSymbolNames().size();
			buf.append(String.format("\t%d: addr=%d args=%d locals=%d type=%d %d/%s\n",
				f.getSymbolIndex(), f.address, numArgs, numLocalsAndArgs-numArgs,
				f.getType().getVMTypeIndex(), s.length(), s));
		}
		buf.append(String.format("%d structs\n", symtab.structs.size()));
		for (String s : symtab.structs.keySet()) {
			StructSymbol structSym = symtab.structs.get(s);
			int numFields = structSym.getAllSymbols().size();
			buf.append(String.format("%d: fields=%d %d/%s\n",
				structSym.getSymbolIndex(), numFields, s.length(), s));
			for (String fs : structSym.getFields().keySet()) {
				FieldSymbol fsym = (FieldSymbol)structSym.getFields().get(fs);
				buf.append(String.format("\t%d: type=%d %d/%s\n",
					fsym.getSymbolIndex(), fsym.getType().getVMTypeIndex(), fs.length(), fs));
			}
		}
		buf.append(String.format("%d globals\n", symtab.globals.size()));
		for (VariableSymbol v : symtab.globals.values()) {
			buf.append(String.format("\t%d: type=%d %d/%s\n",
				v.getSymbolIndex(), v.getType().getVMTypeIndex(), v.name.length(), v.name));
		}

		StringBuilder codeS = serializedCode(code);
		buf.append(String.format("%d instr, %d bytes\n", code.instructions().size(), code.sizeBytes()));
		buf.append(codeS);
		return buf.toString();
	}

	public Code genBytecode() {
		BytecodeGenerator bgen = new BytecodeGenerator(tool, symtab);
		bgen.visit(symtab.tree);
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
			FunctionSymbol fsym = symtab.functions.get(fname);
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
