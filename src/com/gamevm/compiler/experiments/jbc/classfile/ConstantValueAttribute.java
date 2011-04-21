package com.gamevm.compiler.experiments.jbc.classfile;

public class ConstantValueAttribute extends Attribute {
	
	public int constantValueIndex;

	public ConstantValueAttribute(int constantValueIndex) {
		super();
		this.constantValueIndex = constantValueIndex;
	}
	
	

}
