package com.gamevm.compiler.assembly;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface CodeIOFactory<I extends Instruction> {
	
	public CodeReader<I> createCodeReader(InputStream input) throws IOException;
	
	public CodeWriter<I> createCodeWriter(OutputStream input) throws IOException;

}
