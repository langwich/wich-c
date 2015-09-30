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

import wich.codegen.ModelWalker;

import java.util.ArrayList;
import java.util.List;

/** A block of variable definitions and statements not at the outermost
 *  script level.  Split apart the defs from inits of variables.
 */
public class Block extends Stat {
	/** Track vardefs separately. Using add() method ensures this.
	 */
	@ModelElement public List<VarDefStat> varDefs  = new ArrayList<>();
	@ModelElement public List<Stat> stats    	   = new ArrayList<>();

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

	public List<OutputModelObject> getStatementsNoVarDefs() {
		return ModelWalker.findAll(this, (o) -> !(o instanceof VarDefStat));
	}
}
