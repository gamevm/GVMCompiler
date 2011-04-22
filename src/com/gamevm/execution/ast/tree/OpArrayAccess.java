package com.gamevm.execution.ast.tree;

import com.gamevm.execution.ast.builtin.ArrayInstance;
import com.gamevm.utils.StringFormatter;

public class OpArrayAccess<T> extends Expression<T> {

	private Expression<ArrayInstance> left;
	private Expression<Integer> indexExpression;

	public OpArrayAccess(Expression<ArrayInstance> left,
			Expression<Integer> indexExpression) {
		this.left = left;
		this.indexExpression = indexExpression;
	}

	@Override
	public String toString(int ident) {
		return String.format("%s%s[%s]", StringFormatter.generateWhitespaces(ident), left.toString(0), indexExpression.toString(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public T evaluate() throws InterruptedException {
		super.evaluate();
		return (T)left.evaluate().get(indexExpression.evaluate());
	}

	@Override
	public void assign(T value) throws IllegalStateException, InterruptedException {
		left.evaluate().set(indexExpression.evaluate(), value);
	}
	
	@Override
	public String toString() {
		return toString(0);
	}

}
