package com.gamevm.compiler.assembly;

import java.io.DataOutputStream;

public interface CodeWriter<I> {

	public void writeInstruction(DataOutputStream output, I instruction);
	
}
