package com.gamevm.compiler.assembly;

import java.io.DataOutputStream;
import java.io.IOException;

import com.gamevm.utils.StringFormatter;

public class ClassDeclaration {

	protected Method[] methods;
	protected Field[] fields;
	protected int modifier;
	
	protected Type[] imports;
	
	protected String name;
	
	public ClassDeclaration(int modifier, String name, Field[] fields, Method[] methods, Type[] imports) {
		this.name = name;
		this.fields = fields;
		this.methods = methods;
		this.modifier = modifier;
		this.imports = imports;
	}
	
	public void write(DataOutputStream output) throws IOException {
		output.writeInt(imports.length);
		for (Type t : imports) {
			output.writeUTF(t.getName());
		}
		output.writeInt(modifier);
		output.writeUTF(name);
		output.writeInt(methods.length);
		for (Method m : methods) {
			output.writeInt(m.getModifier());
			output.writeUTF(m.getReturnType().getName());
			output.writeUTF(m.getName());
			output.writeInt(m.getParameters().length);
			for (Variable v : m.getParameters()) {
				output.writeUTF(v.getType().getName());
				output.writeUTF(v.getName());
			}
		}
		output.writeInt(fields.length);
		for (Field f : fields) {
			output.writeInt(f.getModifier());
			output.writeUTF(f.getType().getName());
			output.writeUTF(f.getName());
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getSimpleName() {
		int i = name.lastIndexOf('.');
		if (i < 0)
			return name;
		else
			return name.substring(i+1);
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
	
	public int getModifier() {
		return modifier;
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
		String methodTerm = (name.equals("<init>") ? "constructor" : "method");
		throw new IllegalArgumentException(String.format("No %s %s(%s) found", methodTerm, name, StringFormatter.printIterable(parameterTypes, ", ")));
	}
	
	public int getMethod(boolean isStatic, String name, Type... parameterTypes) {
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].isStatic() == isStatic && methods[i].getName().equals(name) && methods[i].isAssignmentCompatible(parameterTypes))
				return i;
		}
		String methodTerm = (name.equals("<init>") ? "constructor" : "method");
		throw new IllegalArgumentException(String.format("No static %s %s(%s) found", methodTerm, name, StringFormatter.printIterable(parameterTypes, ", ")));
	}
	
	public int getMethod(int hasAccess, boolean isStatic, String name, Type... parameterTypes) {
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].hasAccess(hasAccess) && methods[i].isStatic() == isStatic && methods[i].getName().equals(name) && methods[i].isAssignmentCompatible(parameterTypes))
				return i;
		}
		String methodTerm = (name.equals("<init>") ? "constructor" : "method");
		throw new IllegalArgumentException(String.format("No %s %s %s(%s) found", methodTerm, Modifier.toString(Modifier.getFlag(hasAccess, isStatic, false)), name, StringFormatter.printIterable(parameterTypes, ", ")));
	}
	
	public int getField(String name) {
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals(name)) {
				return i;
			}
		}
		return -1;
	}
	
	public int getField(boolean isStatic, String name) {
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].isStatic() && fields[i].getName().equals(name)) {
				return i;
			}
		}
		return -1;
	}
	
	public Type[] getImports() {
		return imports;
	}
}
