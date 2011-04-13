package com.gamevm.execution.ast.tree;

import com.gamevm.utils.StringFormatter;

public class Assignment<T> extends NotAddressable<T> {

	private Expression<T> lvalue;
	private Expression<T> rvalue;
	
	public Assignment(Expression<T> lvalue, Expression<T> rvalue) {
		this.lvalue = lvalue;
		this.rvalue = rvalue;
	}
	
	@Override
	public String toString(int ident) {
		return StringFormatter.generateWhitespaces(ident) + lvalue.toString(0) + " = " + rvalue.toString(0);
	}

	@Override
	public T evaluate() {
		final T v = rvalue.evaluate();
		lvalue.assign(v);
		return v;
	}

}
