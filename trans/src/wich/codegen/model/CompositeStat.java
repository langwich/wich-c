package wich.codegen.model;

import java.util.ArrayList;
import java.util.List;

/** A way to return multiple model objects from a single visitor method */
public class CompositeStat extends Stat {
	@ModelElement public List<Stat> modelObjects = new ArrayList<>();
	public void add(Stat stat) { modelObjects.add(stat); }

	public CompositeStat(Stat... stats) {
		for (Stat o : stats) add(o);
	}
}
