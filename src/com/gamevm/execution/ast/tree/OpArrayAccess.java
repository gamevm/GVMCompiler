package com.gamevm.execution.ast.tree;

import com.gamevm.utils.StringFormatter;

public class OpArrayAccess<T> implements Expression<T> {

	private Expression<T[]> left;
	private Expression<Integer> indexExpression;

	public OpArrayAccess(Expression<T[]> left,
			Expression<Integer> indexExpression) {
		this.left = left;
		this.indexExpression = indexExpression;
	}

	@Override
	public String toString(int ident) {
		return String.format("%s%s[%s]", StringFormatter.generateWhitespaces(ident), left.toString(0), indexExpression.toString(0));
	}

	@Override
	public T evaluate() {
		return left.evaluate()[indexExpression.evaluate()];
	}

	@Override
	public void assign(T value) throws IllegalStateException {
		left.evaluate()[indexExpression.evaluate()] = value;
	}

}
