package com.gamevm.execution.ast;


public class NativeClassInstance extends ClassInstance {

	public NativeClassInstance(String className) {
		super(Environment.getNativeClass(className));
	}

}
