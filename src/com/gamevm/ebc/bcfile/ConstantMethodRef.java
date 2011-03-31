package com.gamevm.ebc.bcfile;

public class ConstantMethodRef extends Constant {
	
	private int classIndex;
	private int nameAndType;
	
	public ConstantMethodRef(int classIndex, int nameAndType) {
		super();
		this.classIndex = classIndex;
		this.nameAndType = nameAndType;
	}
	
	

}
