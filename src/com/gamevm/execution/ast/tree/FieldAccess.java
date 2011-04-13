package com.gamevm.execution.ast.tree;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.execution.ast.ClassInstance;
import com.gamevm.execution.ast.Environment;
import com.gamevm.utils.StringFormatter;

public class FieldAccess<T> implements Expression<T> {
	
	private ClassDeclaration clazz;
	private Expression<ClassInstance> thisClass;
	private int fieldIndex;

	public FieldAccess(ClassDeclaration clazz,
			Expression<ClassInstance> thisClass, int fieldIndex) {
		this.clazz = clazz;
		this.thisClass = thisClass;
		this.fieldIndex = fieldIndex;
	}

	@Override
	public String toString(int ident) {
		if (thisClass != null)
			return String.format("%s%s.%s", StringFormatter.generateWhitespaces(ident), thisClass.toString(0), clazz.getField(fieldIndex).getName());
		else
			return String.format("%s%s", StringFormatter.generateWhitespaces(ident), clazz.getField(fieldIndex).getName());
	}
	
	private ClassInstance getThis() {
		return (thisClass != null) ? thisClass.evaluate() : null;
	}

	@Override
	public T evaluate() {
		return Environment.getField(getThis(), fieldIndex);
	}

	@Override
	public void assign(T value) throws IllegalStateException {
		Environment.setField(getThis(), fieldIndex, value);
	}

}
