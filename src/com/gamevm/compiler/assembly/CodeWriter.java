package com.gamevm.compiler.assembly;

import java.io.IOException;
import java.io.OutputStream;

public interface CodeWriter<I> {

	public void writeInstruction(I instruction) throws IOException;
	
	public void open(OutputStream stream) throws IOException;
	
	public void close() throws IOException;
	
}
