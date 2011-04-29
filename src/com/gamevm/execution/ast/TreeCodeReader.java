package com.gamevm.execution.ast;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import com.gamevm.compiler.assembly.CodeReader;
import com.gamevm.execution.ast.tree.TreeCodeInstruction;

public class TreeCodeReader implements CodeReader<TreeCodeInstruction> {
	
	private ObjectInputStream input;

	@Override
	public TreeCodeInstruction readInstruction() throws IOException {
		try {
			return (TreeCodeInstruction)input.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void open(InputStream stream) throws IOException {
		input = new ObjectInputStream(stream);
	}

	@Override
	public void close() throws IOException {}

}
