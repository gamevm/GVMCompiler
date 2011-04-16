package com.gamevm.execution.ast;

public class StringInstance extends ClassInstance {
	
	private String s;

	public StringInstance(StringClass clazz) {
		super(clazz);
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

}
