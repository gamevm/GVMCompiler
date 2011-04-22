package com.gamevm.execution.ast.tree;

public abstract class NotAddressable<T> extends Expression<T> {

	public void assign(T value) throws IllegalStateException {
		throw new IllegalStateException("This expression is not an L-value");
	}
	
	
	
}
