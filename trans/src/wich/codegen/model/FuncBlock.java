package wich.codegen.model;

public class FuncBlock extends Block {
	public Func enclosingFunc;

	public FuncBlock(Block enclosingBlock) {
		super(enclosingBlock, FUNC_BLOCK_NUMBER);
	}
}
