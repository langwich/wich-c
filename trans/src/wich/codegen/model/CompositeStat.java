package wich.codegen.model;

import java.util.ArrayList;
import java.util.List;

/** A way to return multiple model objects from a single visitor method */
public class CompositeStat extends Stat {
	@ModelElement public List<Stat> modelObjects = new ArrayList<>();
	public void add(Stat stat) { modelObjects.add(stat); }
	public void addAll(CompositeStat stat) { modelObjects.addAll(stat.modelObjects); }

	public CompositeStat(Stat... stats) {
		if ( stats!=null ) {
			for (Stat o : stats) {
				if ( o instanceof CompositeStat ) {
					addAll((CompositeStat)o);
				}
				add(o);
			}
		}
	}
}
