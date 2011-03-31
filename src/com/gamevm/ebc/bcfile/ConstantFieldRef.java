package com.gamevm.ebc.bcfile;

public class ConstantFieldRef extends Constant {
	
	private int classIndex;
	private int nameAndType;
	
	public ConstantFieldRef(int classIndex, int nameAndType) {
		super();
		this.classIndex = classIndex;
		this.nameAndType = nameAndType;
	}

}
