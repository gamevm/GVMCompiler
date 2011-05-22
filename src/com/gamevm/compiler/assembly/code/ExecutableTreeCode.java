package com.gamevm.compiler.assembly.code;

import com.gamevm.execution.ast.tree.CodeNode;

public class ExecutableTreeCode extends TreeCode<CodeNode> {
	
	private int maxLocals;
	
	public ExecutableTreeCode() {
		maxLocals = 0;
	}
	
	public ExecutableTreeCode(int maxLocals) {
		this.maxLocals = maxLocals;
	}
	
	public int getMaxLocals() {
		return maxLocals;
	}

	@Override
	public String toString(int indent) {
		return getRoot().toString(indent);
	}

}
