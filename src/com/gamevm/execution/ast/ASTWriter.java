package com.gamevm.execution.ast;

import java.io.DataOutputStream;

import com.gamevm.compiler.assembly.CodeWriter;
import com.gamevm.execution.ast.tree.Statement;

public class ASTWriter implements CodeWriter<Statement> {

	@Override
	public void writeInstruction(DataOutputStream output, Statement instruction) {
		// ignore for now;
	}

}
