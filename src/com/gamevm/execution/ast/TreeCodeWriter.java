package com.gamevm.execution.ast;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.gamevm.compiler.assembly.CodeWriter;
import com.gamevm.execution.ast.tree.Statement;

public class TreeCodeWriter implements CodeWriter<Statement> {
	
	private ObjectOutputStream output;
	
	@Override
	public void writeInstruction(Statement instruction) throws IOException {
		output.writeObject(instruction);
	}

	@Override
	public void open(OutputStream stream) throws IOException {
		output = new ObjectOutputStream(stream);
	}

	@Override
	public void close() throws IOException {}

}
