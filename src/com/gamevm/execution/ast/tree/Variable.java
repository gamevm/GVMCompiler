package com.gamevm.execution.ast.tree;

import com.gamevm.execution.ast.Environment;

public class Variable extends Expression {

	private static final long serialVersionUID = 1L;
	private int index;
	private String name;
	
	public Variable(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	@Override
	public String toString(int ident) {
		return String.format("%s{$%d}", name, index);
	}

	@Override
	public Object evaluate() throws InterruptedException {
		super.evaluate();
		return Environment.getInstance().getValue(index);
	}

	@Override
	public void assign(Object value) throws IllegalStateException {
		Environment.getInstance().setValue(index, value);
	}
	
	@Override
	public String toString() {
		return toString(0);
	}

}
