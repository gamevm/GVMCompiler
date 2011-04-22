package com.gamevm.compiler.translator.ast;

import com.gamevm.compiler.assembly.Type;

public class Symbol {
	
	private String name;
	private int index;
	private Type type;
	
	public Symbol(String name, int index, Type type) {
		super();
		this.name = name;
		this.index = index;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}
	
	public Type getType() {
		return type;
	}

}
