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

import org.stringtemplate.v4.ST;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import static wich.errors.ErrorSeverity.WARNING;

public class WichErrorHandler {

	protected boolean terminate = false;

	protected Queue<String> errQueue = new LinkedList<>();

	// aggregate error messages. set
	public void aggregate(ErrorType type, String... args) {
		try {
			String msg = getErrorMessage(type, args);
			errQueue.offer(type.getSeverity().getName() + ": " + msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// aggregate error messages.
	public void aggregate(ErrorType type, Exception e, String... args) {
		try {
			String msg = getErrorMessage(type, args);
			errQueue.offer(type.getSeverity().getName() + ": " + msg + "\n" + Arrays.toString(e.getStackTrace()));
		} catch (Exception _e) {
			_e.printStackTrace();
		}
	}

	protected String getErrorMessage(ErrorType type, String[] args) {
		ST template = new ST(type.getMessageTemplate());
		for (int i = 0; i < args.length; ++i) {
			template.add("arg" + String.valueOf(i + 1), args[i]);
		}
		if (type.severity.ordinal() > WARNING.ordinal()) {
			terminate = true;
		}
		return template.render();
	}

	public int getErrorNum() {
		return errQueue.size();
	}

	// destructive operation, will leave the error message queue empty.
	public String toString() {
		StringBuilder sb = new StringBuilder();
		while (!errQueue.isEmpty()) sb.append(errQueue.poll()).append("\n");
		return sb.toString();
	}
}
