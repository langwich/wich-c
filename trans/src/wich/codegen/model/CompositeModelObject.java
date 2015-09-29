package wich.codegen.model;

import java.util.ArrayList;
import java.util.List;

/** A way to return multiple model objects from a single visitor method */
public class CompositeModelObject extends Stat {
	@ModelElement public List<OutputModelObject> modelObjects = new ArrayList<>();
	public void add(OutputModelObject stat) { modelObjects.add(stat); }
	public void addAll(CompositeModelObject stat) { modelObjects.addAll(stat.modelObjects); }

	public CompositeModelObject(OutputModelObject... objects) {
		if ( objects!=null ) {
			for (OutputModelObject o : objects) {
				if ( o instanceof CompositeModelObject) { // flatten
					addAll((CompositeModelObject)o);
				}
				add(o);
			}
		}
	}
}
