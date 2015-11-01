package wich.codegen.bytecode;

import java.util.ArrayList;
import java.util.List;

public class CodeBlock extends ArrayList<Instr> implements Code { // an alias really
    @Override
    public List<Instr> instructions() {
        return this;
    }

    @Override
    public int sizeBytes() {
        int bytes = 0;
        for (Instr I : instructions()) {
            bytes += I.size;
        }
        return bytes;
    }

    @Override
    public boolean add(Code code) {
        if (code != Code.None) {
            return super.addAll(code.instructions());
        }
        return false;
    }

    @Override
    public Code join(Code next) {
        if (next != Code.None) {
            add(next);
        }
        return this;
    }

    public static Code join(Code... next) {
        CodeBlock blk = new CodeBlock();
        for (Code c : next) {
            if ( c!=Code.None ) {
                blk.add(c);
            }
        }
        return blk;
    }

    public static CodeBlock of(final Instr I) {
        return new CodeBlock() {{add(I);}};
    }
}
