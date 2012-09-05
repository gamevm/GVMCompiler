package com.gamevm.execution.ast;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import com.gamevm.compiler.assembly.CodeReader;
import com.gamevm.execution.ast.tree.CodeNode;

public class TreeCodeReader implements CodeReader<CodeNode> {
	
	private ObjectInputStream input;

	@Override
	public CodeNode readInstruction() throws IOException {
		try {
			return (CodeNode)input.readObject();
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
