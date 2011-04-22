package com.gamevm.compiler.assembly;

public class Modifier {
	
	public static final String[] accessStrings = { "public ", "", "protected ", "private " };
	
	public static final int PUBLIC = 0;
	public static final int DEFAULT = 1;
	public static final int PROTECTED = 2;
	public static final int PRIVATE = 3;
	
	
	public static final int ACCESS_MASK = 0x03;
	
	public static final int STATIC = 4;
	public static final int FINAL = 8;
	
	public static int getAccessModifier(int flag) {
		return (flag & ACCESS_MASK);
	}
	
	public static boolean isStatic(int flag) {
		return (flag & STATIC) > 0;
	}
	
	public static boolean isFinal(int flag) {
		return (flag & FINAL) > 0;
	}
	
	public static int getFlag(int accessModifier, boolean isStatic, boolean isFinal) {
		return accessModifier | (((isStatic)?1:0)<<2) | (((isFinal)?1:0)<<3);
	}
	
	public static String toString(int flag) {
		return String.format("%s%s%s", accessStrings[getAccessModifier(flag)], (isStatic(flag)?"static ":""), (isFinal(flag)?" final ":""));
	}

}
