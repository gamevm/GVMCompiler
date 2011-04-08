package com.gamevm.compiler.experiments.jbc.classfile;

public class Method {
	
	private int accessFlags;
	private int nameIndex;
	private int descriptorIndex;
	private CodeAttribute code;
	private ExceptionsAttribute exceptions;

	public Method(int accessFlags, int nameIndex, int descriptorIndex,
			Attribute[] attributes) {
		super();
		this.accessFlags = accessFlags;
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
		
		for (Attribute a : attributes) {
			if (a instanceof CodeAttribute)
				code = (CodeAttribute)a;
			else if (a instanceof ExceptionsAttribute) {
				exceptions = (ExceptionsAttribute)a;
			}
		}
		
	}
	
	public CodeAttribute getCode() {
		return code;
	}
	
	public int getNameIndex() {
		return nameIndex;
	}

}
