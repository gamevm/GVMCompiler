package com.gamevm.execution.ast;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import com.gamevm.compiler.assembly.CodeReader;
import com.gamevm.execution.ast.tree.Statement;

public class TreeCodeReader implements CodeReader<Statement> {
	
	private ObjectInputStream input;
	
	public TreeCodeReader(InputStream stream) throws IOException {
		input = new ObjectInputStream(stream);
	}

	@Override
	public Statement readInstruction() throws IOException {
		try {
			return (Statement)input.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
	}

}
