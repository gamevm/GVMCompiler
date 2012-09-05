package com.gamevm.execution.ast.builtin;

import com.gamevm.execution.ast.NativeClassInstance;

public class StringInstance extends NativeClassInstance {
	
	String s;

	public StringInstance() throws SecurityException, NoSuchMethodException {
		this("");
	}
	
	public StringInstance(String value) {
		super("gc.String");
		this.s = value;
	}
	
	public String getInternal() {
		return s;
	}
	
	@Override
	public String toString() {
		return s;
	}
	
	public int length() {
		return s.length();
	}
	
	public ArrayInstance toCharArray() {
		char[] carr = s.toCharArray();
		Character[] ocarr = new Character[carr.length];
		for (int i = 0; i < carr.length; i++) {
			ocarr[i] = carr[i];
		}
		return new ArrayInstance(ocarr);
	}

}
