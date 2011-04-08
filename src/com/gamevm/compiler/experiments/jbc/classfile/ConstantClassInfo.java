package com.gamevm.compiler.experiments.jbc.classfile;

public class ConstantClassInfo extends Constant {
	
	private int nameIndex;
	
	public ConstantClassInfo(int nameIndex) {
		this.nameIndex = nameIndex;
	}

}
