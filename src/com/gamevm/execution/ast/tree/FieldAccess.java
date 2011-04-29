package com.gamevm.execution.ast.tree;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.execution.ast.ClassInstance;
import com.gamevm.execution.ast.Environment;

public class FieldAccess extends Expression {
	
	private static final long serialVersionUID = 1L;
	private String fieldName;
	private Expression thisClass;
	private int fieldIndex;

	public FieldAccess(ClassDeclaration clazz,
			Expression thisClass, int fieldIndex) {
		this.fieldName = clazz.getField(fieldIndex).getName();		
		this.thisClass = thisClass;
		this.fieldIndex = fieldIndex;
	}

	@Override
	public String toString(int ident) {
		if (thisClass != null)
			return String.format("%s.%s", thisClass.toString(0), fieldName);
		else
			return String.format("%s", fieldName);
	}
	
	private ClassInstance getThis() throws InterruptedException {
		return (ClassInstance)((thisClass != null) ? thisClass.evaluate() : null);
	}

	@Override
	public Object evaluate() throws InterruptedException {
		super.evaluate();
		return Environment.getInstance().getField(getThis(), fieldIndex);
	}

	@Override
	public void assign(Object value) throws IllegalStateException, InterruptedException {
		Environment.getInstance().setField(getThis(), fieldIndex, value);
	}
	
	@Override
	public String toString() {
		return toString(0);
	}

}
