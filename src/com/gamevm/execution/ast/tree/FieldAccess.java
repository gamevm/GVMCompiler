package com.gamevm.execution.ast.tree;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.execution.ast.ClassInstance;
import com.gamevm.execution.ast.Environment;
import com.gamevm.utils.StringFormatter;

public class FieldAccess<T> extends Expression<T> {
	
	private String parentClassName;
	private String fieldName;
	private Expression<ClassInstance> thisClass;
	private int fieldIndex;

	public FieldAccess(ClassDeclaration clazz,
			Expression<ClassInstance> thisClass, int fieldIndex) {
		this.parentClassName = clazz.getName();
		this.fieldName = clazz.getField(fieldIndex).getName();		
		this.thisClass = thisClass;
		this.fieldIndex = fieldIndex;
	}

	@Override
	public String toString(int ident) {
		if (thisClass != null)
			return String.format("%s%s.%s", StringFormatter.generateWhitespaces(ident), thisClass.toString(0), fieldName);
		else
			return String.format("%s%s", StringFormatter.generateWhitespaces(ident), fieldName);
	}
	
	private ClassInstance getThis() throws InterruptedException {
		return (thisClass != null) ? thisClass.evaluate() : null;
	}

	@Override
	public T evaluate() throws InterruptedException {
		super.evaluate();
		return Environment.getInstance().getField(getThis(), fieldIndex);
	}

	@Override
	public void assign(T value) throws IllegalStateException, InterruptedException {
		Environment.getInstance().setField(getThis(), fieldIndex, value);
	}
	
	@Override
	public String toString() {
		return toString(0);
	}

}
