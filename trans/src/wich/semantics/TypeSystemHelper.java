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
package wich.semantics;

import org.antlr.symtab.PrimitiveType;

public class TypeSystemHelper {
	// ---------------------------- Result Type Table ----------------------------

	protected static final PrimitiveType[][] arithmeticResultTable = new PrimitiveType[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {null,      null,       null,       null,       null},
	/*float*/	{null,      null,       null,       null,       null},
	/*string*/	{null,      null,       null,       null,       null},
	/*vector*/	{null,      null,       null,       null,       null},
	/*boolean*/	{null,      null,       null,       null,       null}
	};

	protected static final PrimitiveType[][] arithmeticStrResultTable = new PrimitiveType[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {null,      null,       null,       null,       null},
	/*float*/	{null,      null,       null,       null,       null},
	/*string*/	{null,      null,       null,       null,       null},
	/*vector*/	{null,      null,       null,       null,       null},
	/*boolean*/	{null,      null,       null,       null,       null}
	};

	protected static final PrimitiveType[][] relationalResultTable = new PrimitiveType[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {null,      null,       null,       null,       null},
	/*float*/	{null,      null,       null,       null,       null},
	/*string*/	{null,      null,       null,       null,       null},
	/*vector*/	{null,      null,       null,       null,       null},
	/*boolean*/	{null,      null,       null,       null,       null}
	};

	protected static final PrimitiveType[][] relationalEqualityResultTable = new PrimitiveType[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {null,      null,       null,       null,       null},
	/*float*/	{null,      null,       null,       null,       null},
	/*string*/	{null,      null,       null,       null,       null},
	/*vector*/	{null,      null,       null,       null,       null},
	/*boolean*/	{null,      null,       null,       null,       null}
	};

	protected static final PrimitiveType[][] LogicalResultTable = new PrimitiveType[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {null,      null,       null,       null,       null},
	/*float*/	{null,      null,       null,       null,       null},
	/*string*/	{null,      null,       null,       null,       null},
	/*vector*/	{null,      null,       null,       null,       null},
	/*boolean*/	{null,      null,       null,       null,       null}
	};

	// ---------------------------- Type Promotion Table ----------------------------

	protected static final PrimitiveType[][] arithmeticPromoteFromTo = new PrimitiveType[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {null,      null,       null,       null,       null},
	/*float*/	{null,      null,       null,       null,       null},
	/*string*/	{null,      null,       null,       null,       null},
	/*vector*/	{null,      null,       null,       null,       null},
	/*boolean*/	{null,      null,       null,       null,       null}
	};

	protected static final PrimitiveType[][] arithmeticStrPromoteFromTo = new PrimitiveType[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {null,      null,       null,       null,       null},
	/*float*/	{null,      null,       null,       null,       null},
	/*string*/	{null,      null,       null,       null,       null},
	/*vector*/	{null,      null,       null,       null,       null},
	/*boolean*/	{null,      null,       null,       null,       null}
	};

	protected static final PrimitiveType[][] relationalPromoteFromTo = new PrimitiveType[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {null,      null,       null,       null,       null},
	/*float*/	{null,      null,       null,       null,       null},
	/*string*/	{null,      null,       null,       null,       null},
	/*vector*/	{null,      null,       null,       null,       null},
	/*boolean*/	{null,      null,       null,       null,       null}
	};

	protected static final PrimitiveType[][] relationalEqualityPromoteFromTo = new PrimitiveType[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {null,      null,       null,       null,       null},
	/*float*/	{null,      null,       null,       null,       null},
	/*string*/	{null,      null,       null,       null,       null},
	/*vector*/	{null,      null,       null,       null,       null},
	/*boolean*/	{null,      null,       null,       null,       null}
	};

	protected static final PrimitiveType[][] LogicalPromoteFromTo = new PrimitiveType[][] {
	/*           int        float       string      vector      boolean */
	/*int*/	    {null,      null,       null,       null,       null},
	/*float*/	{null,      null,       null,       null,       null},
	/*string*/	{null,      null,       null,       null,       null},
	/*vector*/	{null,      null,       null,       null,       null},
	/*boolean*/	{null,      null,       null,       null,       null}
	};
}
