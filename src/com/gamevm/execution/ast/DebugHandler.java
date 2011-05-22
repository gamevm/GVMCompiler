package com.gamevm.execution.ast;

import com.gamevm.compiler.assembly.Instruction;
import com.gamevm.compiler.parser.ASTNode;

public interface DebugHandler {
	
	public void debug(Instruction i, ASTNode debugInformation);

}
