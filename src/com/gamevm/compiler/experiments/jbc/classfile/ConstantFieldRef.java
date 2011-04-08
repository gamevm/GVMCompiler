package com.gamevm.compiler.experiments.jbc.classfile;

public class ConstantFieldRef extends Constant {
	
	private int classIndex;
	private int nameAndType;
	
	public ConstantFieldRef(int classIndex, int nameAndType) {
		super();
		this.classIndex = classIndex;
		this.nameAndType = nameAndType;
	}

}
