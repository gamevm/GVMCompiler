package com.gamevm.ebc.execution;

public class Frame {
	
	private Object[] localVariables;
	
	public Frame(Object[] localVariables) {
		this.localVariables = localVariables;
	}
	
	public Object[] getLocalVariables() {
		return localVariables;
	}

}
