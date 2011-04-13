package com.gamevm.execution.ast.tree;

import com.gamevm.execution.ast.Environment;
import com.gamevm.utils.StringFormatter;

public class Variable<T> implements Expression<T> {

	private int index;
	
	public Variable(int index) {
		this.index = index;
	}
	
	@Override
	public String toString(int ident) {
		return String.format("%s$%s", StringFormatter.generateWhitespaces(ident), String.valueOf(index));
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
