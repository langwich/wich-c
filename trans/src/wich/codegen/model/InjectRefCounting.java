package wich.codegen.model;

public class InjectRefCounting {
	public OutputModelObject visitEveryModelObject(OutputModelObject o) {
		System.out.println("visit every node: "+o.getClass().getSimpleName());
		return null;
	}

	public OutputModelObject visit(File f) {
		System.out.println("visit file");
		return null;
	}

	public OutputModelObject visit(AssignStat assign) {
		System.out.println("visit assignment");
		return null;
	}

	public OutputModelObject visit(VarInitStat assign) {
		System.out.println("visit assignment for var init");
		return null;
	}

	public OutputModelObject visit(Func func) {
		System.out.println("visit func");
		return null;
	}
}
