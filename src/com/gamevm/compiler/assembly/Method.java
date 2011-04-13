package com.gamevm.compiler.assembly;

import com.gamevm.utils.StringFormatter;

public class Method {
	
	private Type returnType;
	private String name;
	private Variable[] parameters;
	
	private int modifier;
	
	public Method(int modifier, Type returnType, String name, Variable... parameters) {
		this.returnType = returnType;
		this.name = name;
		this.parameters = parameters;
		this.modifier = modifier;
	}
	
	public boolean isAssignmentCompatible(Type... parameterTypes) {
		if (parameters.length != parameterTypes.length)
			return false;
		
		for (int i = 0; i < parameters.length; i++) {
			if (!parameterTypes[i].isAssignmentCompatible(parameters[i].getType()))
				return false;
		}
		
		return true;
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
	
	public boolean hasAccess(int access) {
		return access >= Modifier.getAccessModifier(modifier);
	}
	
	public boolean isStatic() {
		return Modifier.isStatic(modifier);
	}
	
	public boolean isFinal() {
		return Modifier.isFinal(modifier);
	}
	
	@Override
	public String toString() {
		// TODO: modifier
		StringBuilder b = new StringBuilder();
		
		b.append(Modifier.toString(modifier));
		
		if (returnType != null) {
			b.append(returnType);
			b.append(' ');
		}
		b.append(name);
		b.append('(');
		b.append(StringFormatter.printIterable(parameters, ", "));
		b.append(')');
		return b.toString();
	}

}
