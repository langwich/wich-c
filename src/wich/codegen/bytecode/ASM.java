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
	public Instr vadd()				{ return new Instr("VADD");	}
	public Instr vsub()				{ return new Instr("VSUB");	}
	public Instr vmul()				{ return new Instr("VMUL");	}
	public Instr vdiv()				{ return new Instr("VDIV");	}
	public Instr sadd()				{ return new Instr("SADD");	}
	public Instr vaddi()            { return new Instr("VADDI"); }
	public Instr vaddf()            { return new Instr("VADDF"); }
	public Instr vsubi()            { return new Instr("VSUBI"); }
	public Instr vsubf()            { return new Instr("VSUBF"); }
	public Instr vmuli()            { return new Instr("VMULI"); }
	public Instr vmulf()            { return new Instr("VMULF"); }
	public Instr vdivi()            { return new Instr("VDIVI"); }
	public Instr vdivf()            { return new Instr("VDIVF"); }


	public Instr or()				{ return new Instr("OR"); }
	public Instr and()				{ return new Instr("AND"); }

	public Instr ineg()				{ return new Instr("INEG"); }
	public Instr fneg()				{ return new Instr("FNEG"); }
	public Instr not()				{ return new Instr("NOT"); }

	public Instr i2f()				{ return new Instr("I2F"); }
	public Instr i2s()				{ return new Instr("I2S"); }

	public Instr f2s()				{ return new Instr("F2S"); }
	public Instr v2s()              { return new Instr("V2S"); }

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
	public Instr veq()              { return new Instr("VEQ"); }
	public Instr vneq()             { return new Instr("VNEQ"); }
	public Instr seq()              { return new Instr("SEQ"); }
	public Instr sneq()             { return new Instr("SNEQ"); }
	public Instr slt()				{ return new Instr("SLT"); }
	public Instr sle()				{ return new Instr("SLE"); }
	public Instr sgt()				{ return new Instr("SGT"); }
	public Instr sge()				{ return new Instr("SGE"); }


	public Instr br()				{ return new Instr("BR", 0); }
	public Instr br(int a)			{ return new Instr("BR", a); }  // opnd is relative to start of BR instruction, which is offset 0
	public Instr brf()				{ return new Instr("BRF", 0); }
	public Instr brf(int a)			{ return new Instr("BRF", a); } // opnd arg is relative to next instruction being 0

	public Instr iconst(int v)		{ return new Instr("ICONST", v, 5); }
	public Instr fconst(float v)	{ return new Instr("FCONST", v, 5); }
	public Instr sconst(int i)		{ return new Instr("SCONST",i); }

	public Instr iload(int i)		{ return new Instr("ILOAD", i); }
	public Instr fload(int i)		{ return new Instr("FLOAD", i); }
	public Instr vload(int i)		{ return new Instr("VLOAD", i); }
	public Instr sload(int i)		{ return new Instr("SLOAD", i); }
	public Instr store(int i)		{ return new Instr("STORE", i); }

	public Instr vector()           { return new Instr("VECTOR"); }
	public Instr sload_index()		{ return new Instr("SLOAD_INDEX"); }
	public Instr vload_index()      { return new Instr("VLOAD_INDEX"); }
	public Instr store_index()		{ return new Instr("STORE_INDEX"); }
	public Instr push_dflt_value()	{ return new Instr("PUSH_DFLT_RETV"); }
	public Instr pop()              { return new Instr("POP");  }
	public Instr call(int i)		{ return new Instr("CALL", i); }
	public Instr ret()				{ return new Instr("RET"); }
	public Instr retv()				{ return new Instr("RETV"); }

	public Instr iprint()			{ return new Instr("IPRINT"); }
	public Instr fprint()			{ return new Instr("FPRINT"); }
	public Instr bprint()			{ return new Instr("BPRINT"); }
	public Instr sprint()			{ return new Instr("SPRINT"); }
	public Instr vprint()			{ return new Instr("VPRINT"); }
	public Instr nop()				{ return new Instr("NOP"); }
	public Instr vlen()				{ return new Instr("VLEN"); }
	public Instr slen()				{ return new Instr("SLEN"); }
	public Instr gc_start()			{ return new Instr("GC_START"); }
	public Instr gc_end()			{ return new Instr("GC_END"); }
	public Instr sroot()			{ return new Instr("SROOT"); }
	public Instr vroot()			{ return new Instr("VROOT"); }
	public Instr vec_copy()			{ return new Instr("COPY_VECTOR");}

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
}
