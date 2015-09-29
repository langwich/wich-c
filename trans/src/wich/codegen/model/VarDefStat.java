package wich.codegen.model;

/** Represents a variable definition */
public class VarDefStat extends Stat {
	public final String name;
	@ModelElement public WichType type;

	public VarDefStat(String name, WichType type) {
		this.name = name;
		this.type = type;
	}
}
