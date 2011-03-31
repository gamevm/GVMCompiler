package com.gamevm.ebc.bcfile;

public class ConstantInterfaceMethodRef extends Constant {
	
	private int classIndex;
	private int nameAndType;
	
	public ConstantInterfaceMethodRef(int classIndex, int nameAndType) {
		super();
		this.classIndex = classIndex;
		this.nameAndType = nameAndType;
	}

}
