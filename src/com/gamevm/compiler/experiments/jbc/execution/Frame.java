package com.gamevm.compiler.experiments.jbc.execution;

public class Frame {
	
	private Object[] localVariables;
	
	public Frame(Object[] localVariables) {
		this.localVariables = localVariables;
	}
	
	public Object[] getLocalVariables() {
		return localVariables;
	}

}
