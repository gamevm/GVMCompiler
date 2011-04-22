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
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T callNative(int index) {
		switch (index) {
		case StringClass.METHOD_LENGTH:
			return (T)Integer.valueOf(s.length());
		}
		return null;
	}
	
	protected String getInternal() {
		return s;
	}
	
	@Override
	public String toString() {
		return s;
	}

}
