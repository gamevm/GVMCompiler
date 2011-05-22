package com.gamevm.compiler.assembly.code;

import com.gamevm.compiler.parser.ASTNode;

public class ASTCodeFactory implements CodeFactory<TreeCode<ASTNode>> {

	@Override
	public TreeCode<ASTNode> newCode() {
		return new DefaultTreeCode<ASTNode>();
	}

	@Override
	public int getCodeIdentifier() {
		return Code.AST_TREE;
	}

}
