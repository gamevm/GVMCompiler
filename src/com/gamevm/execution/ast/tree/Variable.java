package com.gamevm.execution.ast.tree;

import com.gamevm.execution.ast.Environment;
import com.gamevm.utils.StringFormatter;

public class Variable<T> implements Expression<T> {

	private int index;
	private String name;
	
	public Variable(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	@Override
	public String toString(int ident) {
		return String.format("%s%s", StringFormatter.generateWhitespaces(ident), name);
	}

	@Override
	public T evaluate() {
		return Environment.getValue(index);
	}

	@Override
	public void assign(T value) throws IllegalStateException {
		Environment.setValue(index, value);
	}

}
