package com.gamevm.compiler.experiments.jbc.classfile;

public class ExceptionsAttribute extends Attribute {
	
	int[] exceptionClassIndices;

	public ExceptionsAttribute(int[] exceptionClassIndices) {
		super();
		this.exceptionClassIndices = exceptionClassIndices;
	}
	
	

}
