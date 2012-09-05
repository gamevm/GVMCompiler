package com.gamevm.compiler.assembly;

import com.gamevm.compiler.Type;

public class Field extends Variable {

	private int modifier;
	
	public Field(int modifier, Type type, String name) {
		super(type, name);
		this.modifier = modifier;
	}
	
	public int getModifier() {
		return modifier;
	}
	
	@Override
	public String toString() {
		// TODO: modifier
		return String.format("%s%s %s", Modifier.toString(modifier), type, name);
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
}
