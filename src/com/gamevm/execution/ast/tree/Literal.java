package com.gamevm.execution.ast.tree;

import com.gamevm.utils.StringFormatter;

public class Literal<T> extends NotAddressable<T> {

	private T value;
	
	public Literal(T value) {
		this.value = value;
	}
	
	@Override
	public String toString(int ident) {
		return String.format("%s%s", StringFormatter.generateWhitespaces(ident), value.toString());
	}

	@Override
	public T evaluate() throws InterruptedException {
		super.evaluate();
		return value;
	}

}
