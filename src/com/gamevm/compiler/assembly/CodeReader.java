package com.gamevm.compiler.assembly;

import java.io.IOException;

public interface CodeReader<I> {
	
	I readInstruction() throws IOException;

}
