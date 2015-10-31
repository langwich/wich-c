package wich.codegen.bytecode;

import java.util.List;

/** Try to make a single type that handles single and multiple instructions
 *  for ease of passing / collecting stuff in codegen visitor.
 */
public interface Code {
    static Code None = new Instr();
    Instr get(int index);
    boolean add(Instr I);
    boolean add(Code code);
    List<Instr> instructions();
    Code join(Code next);
    int sizeBytes(); // sizeBytes in bytes of all instructions in Code
}
