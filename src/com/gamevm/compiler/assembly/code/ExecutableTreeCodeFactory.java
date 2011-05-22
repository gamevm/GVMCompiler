package com.gamevm.compiler.assembly.code;

import com.gamevm.execution.ast.tree.CodeNode;


public class ExecutableTreeCodeFactory implements CodeFactory<TreeCode<CodeNode>> {

	@Override
	public TreeCode<CodeNode> newCode() {
		return new ExecutableTreeCode();
	}

	@Override
	public int getCodeIdentifier() {
		return Code.CODE_TREE;
	}

}
