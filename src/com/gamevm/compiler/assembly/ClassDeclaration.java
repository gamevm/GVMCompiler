package com.gamevm.compiler.assembly;

import com.gamevm.utils.StringFormatter;

public class ClassDeclaration {

	protected Method[] methods;
	protected Field[] fields;
	protected int modifier;
	
	protected String name;
	
	public ClassDeclaration(int modifier, String name, Field[] fields, Method[] methods) {
		this.name = name;
		this.fields = fields;
		this.methods = methods;
		this.modifier = modifier;
	}
	
	public String getName() {
		return name;
	}
	
	public Method[] getMethods() {
		return methods;
	}
	
	public Method getMethod(int i) {
		return methods[i];
	}
	
	public Field[] getFields() {
		return fields;
	}
	
	public Field getField(int i) {
		return fields[i];
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
	
	public Type getType() {
		return Type.getType(name);
	}
	
	public int getMethod(String name, Type... parameterTypes) {
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(name) && methods[i].isAssignmentCompatible(parameterTypes))
				return i;
		}
		throw new IllegalArgumentException(String.format("No method %s(%s) found", name, StringFormatter.printIterable(parameterTypes, ", ")));
	}
	
	public int getField(String name) {
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals(name)) {
				return i;
			}
		}
		throw new IllegalArgumentException(String.format("No field %s found", name));
	}
}
