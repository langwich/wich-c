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
package wich.errors;

import org.antlr.symtab.Utils;
import org.antlr.v4.runtime.Token;
import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static wich.errors.ErrorSeverity.WARNING;

public class WichErrorHandler {
	public int errors = 0;
	public int warnings = 0;

	protected List<String> errorList = new ArrayList<>();

	// aggregate error messages. set
	public void error(Token token, ErrorType type, String... args) {
		try {
			String msg = getErrorMessage(token, type, args);
			errorList.add(type.getSeverity().getName()+": "+msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// aggregate error messages.
	public void error(Token token, ErrorType type, Exception e, String... args) {
		try {
			String msg = getErrorMessage(token, type, args);
			errorList.add(type.getSeverity().getName()+": "+msg+"\n"+Arrays.toString(e.getStackTrace()));
		}
		catch (Exception _e) {
			_e.printStackTrace();
		}
	}

	protected String getErrorMessage(Token token, ErrorType type, String[] args) {
		ST template = new ST(type.getMessageTemplate());
		for (int i = 0; i < args.length; ++i) {
			template.add("arg" + String.valueOf(i + 1), args[i]);
		}
		if ( type.severity == WARNING ) {
			errors++;
		}
		else {
			warnings++;
		}
		String location = "";
		if ( token!=null ) {
			location = "line "+token.getLine()+":"+token.getCharPositionInLine()+" ";
		}
		return location+template.render();
	}

	public int getErrorNum() { return errorList.size(); }

	public String toString() {
		return Utils.join(errorList, "\n ");
	}
}

