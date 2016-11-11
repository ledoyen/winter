package com.github.ledoyen.winter.model;

public class ExpressionAndValue {

	private String expression;
	private String value;
	private String expectedResult;

	public String getExpression() {
		return expression;
	}

	public String getValue() {
		return value != null ? value : expectedResult;
	}
}
