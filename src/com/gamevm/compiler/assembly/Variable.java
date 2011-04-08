package com.gamevm.compiler.assembly;

public class Variable {
	
	protected Type type;
	protected String name;
	
	public Variable(Type type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

}
