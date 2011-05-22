package com.gamevm.execution.ast.builtin;

import com.gamevm.execution.ast.ClassInstance;

public class StringInstance extends ClassInstance {
	
	String s;

	public StringInstance() {
		this("");
	}
	
	public StringInstance(String value) {
		super(StringClass.CLASS);
		this.s = value;
	}
	
	public String getInternal() {
		return s;
	}
	
	@Override
	public String toString() {
		return s;
	}

}
