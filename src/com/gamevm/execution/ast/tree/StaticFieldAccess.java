package com.gamevm.execution.ast.tree;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.execution.ast.Environment;
import com.gamevm.utils.StringFormatter;

public class StaticFieldAccess<T> implements Expression<T> {
	
	private int classIndex;
	private int fieldIndex;
	private String fieldName;

	public StaticFieldAccess(int classIndex, int fieldIndex, String fieldName) {
		this.classIndex = classIndex;
		this.fieldIndex = fieldIndex;
		this.fieldName = fieldName;
	}

	@Override
	public String toString(int ident) {
		return String.format("%s%s", StringFormatter.generateWhitespaces(ident), fieldName);
	}

	@Override
	public T evaluate() {
		return Environment.getStaticField(classIndex, fieldIndex);
	}

	@Override
	public void assign(T value) throws IllegalStateException {
		Environment.setStaticField(classIndex, fieldIndex, value);
	}

}
