package wich.codegen.bytecode;

import java.util.List;

public class Instr implements Code {
	int address = -1;  // computed after code gen
	String opcode;
	Number operand; // int or float
	int size = 1;

	public Instr() {
	}

	public Instr(String opcode) {
		this();
		this.opcode = opcode;
	}

	public Instr(String opcode, Number operand) {
		this(opcode);
		this.operand = operand;
		this.size = 3;
	}

	public Instr(String opcode, Number operand, int size) {
		this(opcode, operand);
		this.size = size;
	}

	@Override
	public Code join(Code next) {
		if ( this==Code.None ) {
			return next;
		}
		if (next != Code.None) {
			Code c = CodeBlock.of(this);
			c.add(next);
			return c;
		}
		return this;
	}

	@Override
	public List<Instr> instructions() {
		return CodeBlock.of(this).instructions();
	}

	@Override
	public Instr get(int index) {
		return this;
	}

	@Override
	public int sizeBytes() {
//		if(size ==1 && opcode == null) {
//			return 0;
//		}
		return size;
	}

	@Override
	public boolean add(Instr I) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(Code code) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		if ( operand!=null ) {
			return opcode+" "+operand;
		}
		return opcode;
	}
}
