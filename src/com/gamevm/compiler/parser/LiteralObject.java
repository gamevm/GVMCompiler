package com.gamevm.compiler.parser;

import com.gamevm.compiler.Type;

public class LiteralObject {
	
	private Object value;
	private Type type;
	
	public LiteralObject(Object value, Type type) {
		this.value = value;
		this.type = type;
	}
	
	public Object getValue() {
		return value;
	}
	
	public Type getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return String.format("{%s:%s}", value, type);
	}

}
