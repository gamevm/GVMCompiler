package com.gamevm.execution.ast.tree;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.execution.ast.Environment;
import com.gamevm.utils.StringFormatter;

public class StaticFieldAccess<T> implements Expression<T> {
	
	private int classIndex;
	private int fieldIndex;

	public StaticFieldAccess(int classIndex, int fieldIndex) {
		this.classIndex = classIndex;
		this.fieldIndex = fieldIndex;
	}

	@Override
	public String toString(int ident) {
		ClassDeclaration d = Environment.getClassInformation(classIndex);
		return String.format("%s%s.%s", StringFormatter.generateWhitespaces(ident), d.getName(), d.getField(fieldIndex).getName());
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
