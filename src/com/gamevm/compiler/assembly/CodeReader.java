package com.gamevm.compiler.assembly;

import java.io.IOException;
import java.io.InputStream;

public interface CodeReader<I> {
	
	I readInstruction() throws IOException;
	
	public void open(InputStream stream) throws IOException;
	
	public void close() throws IOException;

}
