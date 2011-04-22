package com.gamevm.execution.ast.tree;

import com.gamevm.execution.ast.Environment;
import com.gamevm.utils.StringFormatter;

public class Variable<T> extends Expression<T> {

	private int index;
	private String name;
	
	public Variable(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	@Override
	public String toString(int ident) {
		return String.format("%s%s[$%d]", StringFormatter.generateWhitespaces(ident), name, index);
	}

	@Override
	public T evaluate() throws InterruptedException {
		super.evaluate();
		return Environment.getInstance().getValue(index);
	}

	@Override
	public void assign(T value) throws IllegalStateException {
		Environment.getInstance().setValue(index, value);
	}
	
	@Override
	public String toString() {
		return toString(0);
	}

}
