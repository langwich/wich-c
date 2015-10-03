/*
The MIT License (MIT)

Copyright (c) 2015 Terence Parr, Hanzhou Shi, Shuai Yuan, Yuanyuan Zhang

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package wich.codegen.model;

import org.antlr.symtab.Scope;
import org.antlr.symtab.Symbol;
import wich.codegen.CodeGenerator;
import wich.codegen.ModelWalker;
import wich.semantics.symbols.WVariableSymbol;

import java.util.ArrayList;
import java.util.List;

/** A block of variable definitions and statements not at the outermost
 *  script level.  Split apart the defs from inits of variables.
 */
public class Block extends Stat {
	public static final int FUNC_BLOCK_NUMBER = 0;
	public static final int SCRIPT_BLOCK_NUMBER = FUNC_BLOCK_NUMBER;

	/** indexed from 0 and is the block number within the enclosing block */
	public int blockNumber;

	/** What scope in symtab is associated with this block? */
	public Scope scope;

	/** The lexically enclosing block; null if this instanceof FuncBlock or Script */
	public Block enclosingBlock;

	/** Track vardefs separately. Using add() method ensures this. */
	@ModelElement public List<VarDefStat> varDefs  = new ArrayList<>();
	@ModelElement public List<Stat> stats    	   = new ArrayList<>();
	@ModelElement public List<Stat> cleanup    	   = new ArrayList<>();

	public Block(Block enclosingBlock, int blockNumber) {
		this.enclosingBlock = enclosingBlock;
		this.blockNumber = blockNumber;
	}

	public void add(Stat stat) {
		if ( stat instanceof CompositeModelObject ) {
			final List<OutputModelObject> substats = ((CompositeModelObject) stat).modelObjects;
			// cast to force addition even if substats might not be Stat at runtime
			for (OutputModelObject ss : substats) {
				if ( ss instanceof VarDefStat ) {
					varDefs.add((VarDefStat)ss);
				}
				else {
					stats.add((Stat) ss);
				}
			}
		}
		else {
			if ( stat instanceof VarDefStat ) {
				varDefs.add((VarDefStat)stat);
			}
			else {
				stats.add(stat);
			}
		}
	}

	/** How many heap vars in this specific block */
	public int getNumHeapVars() {
		int n = 0;
		for (Symbol s : scope.getSymbols()) {
			if ( s instanceof WVariableSymbol ) {
				WVariableSymbol ws = (WVariableSymbol) s;
				if ( CodeGenerator.isHeapType(ws.getType()) ) {
					n++;
				}
			}
		}
		return n;
	}

	public List<OutputModelObject> getStatementsNoVarDefs() {
		return ModelWalker.findAll(this, (o) -> !(o instanceof VarDefStat));
	}
}
