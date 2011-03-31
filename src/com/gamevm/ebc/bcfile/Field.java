package com.gamevm.ebc.bcfile;

public class Field {

	private int accessFlags;
	private int nameIndex;
	private int descriptorIndex;
	private int constValueIndex;

	public Field(int accessFlags, int nameIndex, int descriptorIndex,
			Attribute[] attributes) {
		super();
		this.accessFlags = accessFlags;
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
		
		for (Attribute a : attributes) {
			if (a instanceof ConstantValueAttribute) {
				constValueIndex = ((ConstantValueAttribute)a).constantValueIndex;
			}
		}
		
	}

}
