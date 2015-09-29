package wich.codegen.model;

public class InjectRefCounting {
	public OutputModelObject visitEveryModelObject(OutputModelObject o) {
		System.out.println("visit every node: "+o.getClass().getSimpleName());
		return o;
	}

	public OutputModelObject visit(File f) {
		System.out.println("visit file");
		return f;
	}

	public OutputModelObject visit(AssignStat assign) {
		System.out.println("visit assignment");
		return assign;
	}

	public OutputModelObject visit(VarInitStat assign) {
		System.out.println("visit assignment for var init");
		return assign;
	}

	public OutputModelObject visit(Func func) {
		System.out.println("visit func");
		return func;
	}
}
