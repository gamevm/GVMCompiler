package com.gamevm.execution.ast;

public class Frame {

	private Object[] values;
	private int stackDeclarationCounter;
	
	public Frame(int size, Object... parameters) {
		values = new Object[size];
		System.arraycopy(parameters, 0, values, 0, parameters.length);
		stackDeclarationCounter = parameters.length;
	}
	
	public Object getVariable(int index) {
		return values[index];
	}
	
	public void setVariable(int index, Object value) {
		values[index] = value;
	}
	
	public Object[] getLocals() {
		return values;
	}
	
	public void addVariable(Object value) {
		values[stackDeclarationCounter++] = value;
	}
	
}
