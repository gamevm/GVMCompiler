package com.gamevm.execution.ast.tree;

public abstract class NotAddressable extends Expression {

	private static final long serialVersionUID = 1L;

	public void assign(Object value) throws IllegalStateException {
		throw new IllegalStateException("This expression is not an L-value");
	}
	
	
	
}
