package com.gamevm.compiler.assembly.code;

import com.gamevm.execution.ast.tree.CodeNode;

public class ExecutableTreeCode extends DefaultTreeCode<CodeNode> {
	
	//private int maxLocals;
	
	public ExecutableTreeCode() {
		//maxLocals = 0;
	}
	
	public ExecutableTreeCode(CodeNode root) {
		super(root);
		//this.maxLocals = root.getMaxLocals();
	}
	
//	public int getMaxLocals() {
//		return maxLocals;
//	}

}
