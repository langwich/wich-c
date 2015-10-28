package wich.codegen.bytecode;

import wich.semantics.SymbolTable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Routines to generate Wich bytecode.
 *
 *  This collects code for all functions as it needs to compute
 *  addresses for function starts.  All code for all functions gets
 *  packed together into code segment.
 */
public class ASM {
	public List<String> instructions = new ArrayList<String>(); // final list of instructions
	public List<String> buffer = new ArrayList<String>(); // build up code until we "commit"/save to a node
	public int ip = 0;				// write to which address next?
	public int instrIndex = 0;		// write to which instruction line next?
	public Map<String, Integer> funcNameToAddr = new LinkedHashMap<String, Integer>();

	/** assume little-endian (yuck) used by x86 and ARM on BeagleBone Black. */
	public boolean littleEndian = true;

	public SymbolTable symtab;

	public ASM(SymbolTable symtab) {
		this.symtab = symtab;
	}

	// These functions add instructions and return the instruction index not ip

	public Instr halt()				{ return new Instr("HALT"); }
	public Instr iadd()				{ return new Instr("IADD"); }
	public Instr isub()				{ return new Instr("ISUB"); }
	public Instr imul()				{ return new Instr("IMUL"); }
	public Instr idiv()				{ return new Instr("IDIV"); }
	public Instr fadd()				{ return new Instr("FADD"); }
	public Instr fsub()				{ return new Instr("FSUB"); }
	public Instr fmul()				{ return new Instr("FMUL"); }
	public Instr fdiv()				{ return new Instr("FDIV"); }
	public Instr or()				{ return new Instr("OR"); }
	public Instr and()				{ return new Instr("AND"); }
	public Instr ineg()				{ return new Instr("INEG"); }
	public Instr fneg()				{ return new Instr("FNEG"); }
	public Instr not()				{ return new Instr("NOT"); }
	public Instr i2f()				{ return new Instr("I2F"); }
	public Instr f2i()				{ return new Instr("F2I"); }
	public Instr i2c()				{ return new Instr("I2C"); }
	public Instr c2i()				{ return new Instr("C2I"); }
	public Instr ieq()				{ return new Instr("IEQ"); }
	public Instr ineq()				{ return new Instr("INEQ"); }
	public Instr ilt()				{ return new Instr("ILT"); }
	public Instr ile()				{ return new Instr("ILE"); }
	public Instr igt()				{ return new Instr("IGT"); }
	public Instr ige()				{ return new Instr("IGE"); }
	public Instr feq()				{ return new Instr("FEQ"); }
	public Instr fneq()				{ return new Instr("FNEQ"); }
	public Instr flt()				{ return new Instr("FLT"); }
	public Instr fle()				{ return new Instr("FLE"); }
	public Instr fgt()				{ return new Instr("FGT"); }
	public Instr fge()				{ return new Instr("FGE"); }
	public Instr isnil()			{ return new Instr("ISNIL"); }
	public Instr br()				{ return new Instr("BR", 0); }
	public Instr br(int a)			{ return new Instr("BR", a); }  // opnd is relative to start of BR instruction, which is offset 0
	public Instr brt()				{ return new Instr("BRT", 0); }
	public Instr brt(int a)			{ return new Instr("BRT", a); }
	public Instr brf()				{ return new Instr("BRF", 0); }
	public Instr brf(int a)			{ return new Instr("BRF", a); } // opnd arg is relative to next instruction being 0
	public Instr iconst(int v)		{ return new Instr("ICONST", v, 5); }
	public Instr fconst(float v)	{ return new Instr("FCONST", v, 5); }
	public Instr cconst(int v)		{ return new Instr("CCONST", v); }
	public Instr sconst(int i)		{ return new Instr("SCONST", i); }
	public Instr iload(int i)		{ return new Instr("ILOAD", i); }
	public Instr fload(int i)		{ return new Instr("FLOAD", i); }
	public Instr pload(int i)		{ return new Instr("PLOAD", i); }
	public Instr cload(int i)		{ return new Instr("CLOAD", i); }
	public Instr store(int i)		{ return new Instr("STORE", i); }
	public Instr load_global(int i)	{ return new Instr("LOAD_GLOBAL", i); }
	public Instr store_global(int i){ return new Instr("STORE_GLOBAL", i); }
	public Instr _new(int i)		{ return new Instr("NEW", i); }
	public Instr free()				{ return new Instr("FREE"); }
	public Instr load_field(int i)	{ return new Instr("LOAD_FIELD", i); }
	public Instr store_field(int i)	{ return new Instr("STORE_FIELD", i); }
	public Instr iarray()			{ return new Instr("IARRAY"); }
	public Instr farray()			{ return new Instr("FARRAY"); }
	public Instr parray()			{ return new Instr("PARRAY"); }
	public Instr load_index()		{ return new Instr("LOAD_INDEX"); }
	public Instr store_index()		{ return new Instr("STORE_INDEX"); }
	public Instr nil()				{ return new Instr("NIL"); }
	public Instr call(int i)		{ return new Instr("CALL", i); }
	public Instr ret()				{ return new Instr("RET"); }
	public Instr retv()				{ return new Instr("RETV"); }
	public Instr iprint()			{ return new Instr("IPRINT"); }
	public Instr fprint()			{ return new Instr("FPRINT"); }
	public Instr pprint()			{ return new Instr("PPRINT"); }
	public Instr cprint()			{ return new Instr("CPRINT"); }
	public Instr nop()				{ return new Instr("NOP"); }

	public void gen(String format, Object... args) {
		buffer.add(String.format(format + "\n", args));
	}

	public void emit(List<String> code) {
		instructions.addAll(code);
	}

	public List<String> commit() {
		ArrayList<String> code = new ArrayList<String>();
		code.addAll(instructions);
		instructions.clear();
		return code;
	}

	//	public String opnd16(int i) {
//		byte[] bytes = intToBytes(i);
//		if ( littleEndian ) {
//			return String.format("0x%02x 0x%02x", bytes[1], bytes[0]);
//		}
//		else {
//			return String.format("0x%02x 0x%02x", bytes[0], bytes[1]);
//		}
//	}
//
//	public String opnd32(String is) {
//		return opnd32(Integer.valueOf(is));
//	}
//
//	public String opnd32(int i) {
//		byte[] bytes = intToBytes(i);
//		if ( littleEndian ) {
//			return String.format("0x%02x 0x%02x 0x%02x 0x%02x", bytes[3], bytes[2], bytes[1], bytes[0]);
//		}
//		else {
//			return String.format("0x%02x 0x%02x 0x%02x 0x%02x", bytes[0], bytes[1], bytes[2], bytes[3]);
//		}
//	}
//
//	public byte[] intStringToBytes(String vs) { // comes out in big-endian form
//		return intToBytes(Integer.valueOf(vs));
//	}
//
//	public byte[] intToBytes(int v) { // comes out in big-endian form
//		return ByteBuffer.allocate(4).putInt(v).array();
//	}
}
