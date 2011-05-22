package com.gamevm.compiler.assembly;

public abstract class AbstractInstruction implements Instruction {
	
	@Override
	public String toString() {
		return toString(0);
	}
	
	public abstract String toString(int ident);

}
