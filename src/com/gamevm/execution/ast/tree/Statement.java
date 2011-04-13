package com.gamevm.execution.ast.tree;

import com.gamevm.compiler.assembly.Instruction;

public interface Statement extends Instruction {

	public void execute();
	
}
