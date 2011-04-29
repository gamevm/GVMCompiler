package com.gamevm.execution.ast.tree;

import com.gamevm.execution.ast.Environment;

public class StaticFieldAccess extends Expression {
	
	private static final long serialVersionUID = 1L;
	private int classIndex;
	private int fieldIndex;
	private String fieldName;
	private String className;

	public StaticFieldAccess(int classIndex, int fieldIndex, String className, String fieldName) {
		this.classIndex = classIndex;
		this.fieldIndex = fieldIndex;
		this.fieldName = fieldName;
		this.className = className;
	}

	@Override
	public String toString(int ident) {
		if (className != null)
			return String.format("%s.%s", className, fieldName);
		else
			return String.format("%s", fieldName);
	}

	@Override
	public Object evaluate() throws InterruptedException {
		super.evaluate();
		return Environment.getInstance().getStaticField(classIndex, fieldIndex);
	}

	@Override
	public void assign(Object value) throws IllegalStateException {
		Environment.getInstance().setStaticField(classIndex, fieldIndex, value);
	}

	@Override
	public String toString() {
		return toString(0);
	}
}
