package com.gamevm.compiler.experiments.jbc.classfile;

public class ConstantMethodRef extends Constant {
	
	private int classIndex;
	private int nameAndType;
	
	public ConstantMethodRef(int classIndex, int nameAndType) {
		super();
		this.classIndex = classIndex;
		this.nameAndType = nameAndType;
	}
	
	

}
