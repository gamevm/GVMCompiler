package com.gamevm.compiler.parser;

public class Type {
	
	public static boolean matches(Class<?> lType, Class<?> rType) {
		return lType.equals(rType) || (Number.class.isAssignableFrom(lType) && Number.class.isAssignableFrom(rType)) || lType.equals(String.class) || rType.equals(String.class);
	}

}
