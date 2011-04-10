package com.gamevm.compiler.assembly;

import java.util.HashMap;
import java.util.Map;

public class Type {
	
	public static final Type BYTE = new Type("_byte");
	public static final Type SHORT = new Type("_short");
	public static final Type INT = new Type("_int");
	public static final Type LONG = new Type("_long");
	public static final Type FLOAT = new Type("_float");
	public static final Type DOUBLE = new Type("_double");
	public static final Type BOOLEAN = new Type("_boolean");
	public static final Type CHAR = new Type("_char");
	
	private static Map<String, Type> typePool = new HashMap<String, Type>();
	
//	{
//		typePool.put(BYTE.getName(), BYTE);
//		typePool.put(SHORT.getName(), SHORT);
//		typePool.put(INT.getName(), INT);
//		typePool.put(LONG.getName(), LONG);
//		typePool.put(FLOAT.getName(), FLOAT);
//		typePool.put(DOUBLE.getName(), DOUBLE);
//		typePool.put(BOOLEAN.getName(), BOOLEAN);
//		typePool.put(CHAR.getName(), CHAR);
//	}
	
	private String name;
	private boolean isPrimitive;
	
	private Type(String name) {
		this.name = name;
		this.isPrimitive = (name.charAt(0) == '_');
	}
	
	public static Type getType(String name) {
		Type t = typePool.get(name);
		if (t == null) {
			t = new Type(name);
			typePool.put(name, t);
		}
		return t;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isPrimitive() {
		return isPrimitive;
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			return name.equals(((Type)obj).name);
		} catch (ClassCastException e) {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public String toString() {
		if (!isPrimitive) {
			return name;
		} else {
			return name.substring(1);
		}
	}
}
