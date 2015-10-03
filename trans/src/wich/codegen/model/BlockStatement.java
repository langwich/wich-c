package wich.codegen.model;

public class BlockStatement extends Stat {
	@ModelElement public Block block;

	public BlockStatement(Block block) {
		this.block = block;
	}
}
