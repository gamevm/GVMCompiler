package com.gamevm.compiler.experiments.jbc.classfile;

public class ConstantInterfaceMethodRef extends Constant {
	
	private int classIndex;
	private int nameAndType;
	
	public ConstantInterfaceMethodRef(int classIndex, int nameAndType) {
		super();
		this.classIndex = classIndex;
		this.nameAndType = nameAndType;
	}

}
