package com.gamevm.compiler.assembly;

public class ClassDeclaration {

	protected Method[] methods;
	protected Field[] fields;
	
	protected String name;
	
	public ClassDeclaration(String name, Field[] fields, Method[] methods) {
		this.name = name;
		this.fields = fields;
		this.methods = methods;
	}
	
	
	public Method getMethod(int i) {
		return methods[i];
	}
	
	public Field getField(int i) {
		return fields[i];
	}
	
}
