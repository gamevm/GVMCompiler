package com.gamevm.compiler.assembly;

import com.gamevm.utils.StringFormatter;

public class Method {
	
	private Type returnType;
	private String name;
	private Variable[] parameters;
	
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
	
	@Override
	public String toString() {
		// TODO: modifier
		StringBuilder b = new StringBuilder();
		if (returnType != null)
			b.append(returnType);
		b.append(' ');
		b.append(name);
		b.append('(');
		b.append(StringFormatter.printIterable(parameters, ", "));
		b.append(')');
		return b.toString();
	}

}
