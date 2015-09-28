package wich.codegen;

import org.antlr.symtab.Scope;
import org.antlr.symtab.Type;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import wich.codegen.model.Block;
import wich.codegen.model.File;
import wich.codegen.model.FloatType;
import wich.codegen.model.IntType;
import wich.codegen.model.OutputModelObject;
import wich.codegen.model.Script;
import wich.codegen.model.StringType;
import wich.codegen.model.VectorType;
import wich.codegen.model.WichType;
import wich.parser.WichBaseVisitor;
import wich.parser.WichParser;
import wich.semantics.SymbolTable;
import wich.semantics.symbols.WBuiltInTypeSymbol;
import wich.semantics.symbols.WString;
import wich.semantics.symbols.WVector;

public class CodeGenerator extends WichBaseVisitor<OutputModelObject> {
	protected STGroup templates;
	protected String fileName;
	protected final SymbolTable symtab;
	protected File file;
	protected Scope currentScope;
	protected Block currentBlock;   // used by expr model construction to track tmp vars needed
	protected int tmpIndex = 1;     // track how many temp vars we create

	public CodeGenerator(String fileName, SymbolTable symtab) {
		this.templates = new STGroupFile("wich.stg");
		this.symtab = symtab;
		this.fileName = fileName;
	}

	public File generate(ParserRuleContext tree) {
		return (File)visit(tree);
	}

	@Override
	public OutputModelObject visitFile(@NotNull WichParser.FileContext ctx) {
		this.file = new File();
		this.file.script = (Script)visit(ctx.script());
		return file;
	}

	@Override
	public OutputModelObject visitScript(@NotNull WichParser.ScriptContext ctx) {
		pushScope(symtab.getGlobalScope());
		Script script = new Script(fileName);
		return script;
	}

	public static boolean isHeapObject(Type type) {
		return type instanceof WString || type instanceof WVector;
	}

	public static WichType getTypeModel(Type type) {
		if ( type instanceof WBuiltInTypeSymbol ) {
			switch ( ((WBuiltInTypeSymbol)type).typename ) {
				case VECTOR :
					return new VectorType();
				case STRING :
					return new StringType();
				case INT :
					return new IntType();
				case FLOAT:
					return new FloatType();
			}
		}
		return null;
	}

	protected void pushScope(Scope s) {currentScope = s;}

	protected void popScope() {currentScope = currentScope.getEnclosingScope();}
}
