package wich.errors;/*
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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class WichErrorHandler {
	public static final int INCOMPATIBLE_OPERAND = 1;
	public static final int INCOMPATIBLE_ASSIGNMENT = 2;
	public static final int INVALID_ELEMENT = 3;
	public static final int INVALID_VECTOR_INDEX = 4;
	public static final int UNDEFINED_TYPE = 5;
	public static final int UNDEFINED_FUNCTION = 6;

	protected static final Map<Integer, Class<?>> _errorMap = new HashMap<>();
	static {
		_errorMap.put(INCOMPATIBLE_OPERAND, IncompOpError.class);
		_errorMap.put(INCOMPATIBLE_ASSIGNMENT, IncompAssignError.class);
		_errorMap.put(INVALID_ELEMENT, InvalidElemError.class);
		_errorMap.put(INVALID_VECTOR_INDEX, InvalidVecIndexError.class);
		_errorMap.put(UNDEFINED_TYPE, UndefinedTypeError.class);
		_errorMap.put(UNDEFINED_FUNCTION, UndefinedError.class);

	}

	protected Queue<Error> errQueue = new LinkedList<>();

	// aggregate error messages. set
	public void aggregate(int type, String customMsg) {
		try {
			Error err = (Error) _errorMap.get(type).getConstructor().newInstance();
			err.setCustomMsg(customMsg);
			errQueue.offer(err);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void dump(OutputStream os, Charset cs) {
		for (Error error : errQueue) {
			try {
				os.write(error.getMsg().getBytes(cs));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		errQueue.forEach((e) -> sb.append(e.getMsg()));
		return sb.toString();
	}
}