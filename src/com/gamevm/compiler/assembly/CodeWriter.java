package com.gamevm.compiler.assembly;

import java.io.DataOutputStream;
import java.io.IOException;

public interface CodeWriter<I> {

	public void writeInstruction(I instruction) throws IOException;
	
}
