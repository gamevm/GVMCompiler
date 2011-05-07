package com.gamevm.compiler.translator;

import com.gamevm.compiler.Type;

public class TypedCode<D> {
	
	private D codeDescriptor;
	private Type type;
	
	public TypedCode(D codeDescriptor, Type type) {
		this.codeDescriptor = codeDescriptor;
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public D getCodeDescriptor() {
		return codeDescriptor;
	}
	
	public boolean isStatement() {
		return (type == null);
	}

}
