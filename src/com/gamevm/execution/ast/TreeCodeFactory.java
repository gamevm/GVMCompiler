package com.gamevm.execution.ast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.gamevm.compiler.assembly.CodeIOFactory;
import com.gamevm.compiler.assembly.CodeReader;
import com.gamevm.compiler.assembly.CodeWriter;
import com.gamevm.execution.ast.tree.Statement;

public class TreeCodeFactory implements CodeIOFactory<Statement> {

	@Override
	public CodeReader<Statement> createCodeReader(InputStream input) throws IOException {
		return new TreeCodeReader(input);
	}

	@Override
	public CodeWriter<Statement> createCodeWriter(OutputStream input) throws IOException {
		return new TreeCodeWriter(input);
	}

}
