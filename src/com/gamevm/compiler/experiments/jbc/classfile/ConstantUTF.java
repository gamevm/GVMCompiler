package com.gamevm.compiler.experiments.jbc.classfile;

public class ConstantUTF extends Constant {
	
	private String value;
	
	public ConstantUTF(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

}
