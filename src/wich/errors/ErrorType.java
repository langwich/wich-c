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


import static wich.errors.ErrorSeverity.ERROR;

public enum ErrorType {
	SYNTAX_ERROR("invalid syntax: <arg1>", ERROR),
	INVALID_LEFT_SIDE_ERROR("left side of assignment must be a variable", ERROR),
	INCOMPATIBLE_ASSIGNMENT_ERROR("incompatible type in assignment (cannot promote from <arg2> to <arg1>)", ERROR),
	INCOMPATIBLE_ARGUMENT_ERROR("incompatible argument type (cannot promote from <arg2> to <arg1>)", ERROR),
	INCORRECT_ARG_NUMBERS("incorrect number of args (<arg1> args expected but <arg2> was given)", ERROR),
	INVALID_CONDITION_ERROR("invalid condition type (boolean expected but <arg1> was given)", ERROR),
	INCOMPATIBLE_OPERAND_ERROR("incompatible operand types (<arg1> <arg2> <arg3>)", ERROR),
	INVALID_OPERAND_ERROR("invalid operand (arg1)", ERROR),
	INVALID_ELEMENT_ERROR("incorrect element type (should be float, but <arg1> was given)", ERROR),
	INVALID_INDEX_ERROR("invalid vector index type (should be int, but <arg1> was given)", ERROR),
	INVALID_OPERATION("invalid operation (<arg1> expected, but <arg2> was given)", ERROR),
	INVALID_TYPE("invalid type for (<arg1>)", ERROR),
	SYMBOL_NOT_FOUND("symbol not found (<arg1>)", ERROR),
	UNDEFINED_FUNCTION("function <arg1> not defined", ERROR),
	INTERNAL_STRINGTEMPLATE_ERROR("internal stringtemplate error: <arg1>", ERROR),

	UNKNOWN_TARGET("Unknown translation target: <arg1>", ERROR);


	protected String template;
	protected ErrorSeverity severity;

	ErrorType(String template, ErrorSeverity severity) {
		this.template = template;
		this.severity = severity;
	}

	public String getMessageTemplate() {
		return template;
	}

	public ErrorSeverity getSeverity() {
		return severity;
	}
}
