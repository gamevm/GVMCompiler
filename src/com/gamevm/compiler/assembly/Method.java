package com.gamevm.compiler.assembly;

public class Method {
	
	private Type returnType;
	private String name;
	private Variable[] parameters;
	
	private int maxLocals;
	
	private Instruction[] code;
	
	private void computeLocals() {
		
	}
	
	public Method(Type returnType, String name, Variable... parameters) {
		this.returnType = returnType;
		this.name = name;
		this.parameters = parameters;
	}

	public Type getReturnType() {
		return returnType;
	}

	public String getName() {
		return name;
	}

	public Variable[] getParameters() {
		return parameters;
	}
	
	

}
