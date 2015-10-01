package wich.codegen.model;

public class FuncBlock extends Block {
	public Func enclosingFunc;

	public FuncBlock() {
		super(FUNC_BLOCK_NUMBER); // the outer block is always
	}
}
