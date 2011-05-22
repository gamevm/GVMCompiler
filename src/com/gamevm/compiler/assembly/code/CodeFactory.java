package com.gamevm.compiler.assembly.code;


public interface CodeFactory<C extends Code> {
	
	public C newCode();
	
	public int getCodeIdentifier();

}
