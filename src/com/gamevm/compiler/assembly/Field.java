package com.gamevm.compiler.assembly;

public class Field extends Variable {

	public Field(Type type, String name) {
		super(type, name);
	}
	
	@Override
	public String toString() {
		// TODO: modifier
		return String.format("%s %s", type, name);
	}

}
