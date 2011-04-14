package com.gamevm.compiler.assembly;

import java.io.DataInputStream;

public interface CodeReader<I> {
	
	I readInstruction(DataInputStream input);

}
