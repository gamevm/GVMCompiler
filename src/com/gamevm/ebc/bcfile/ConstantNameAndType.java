package com.gamevm.ebc.bcfile;

public class ConstantNameAndType extends Constant {
	
	private int nameIndex;
	private int descriptorIndex;
	
	public ConstantNameAndType(int nameIndex, int descriptorIndex) {
		super();
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
	}

}
