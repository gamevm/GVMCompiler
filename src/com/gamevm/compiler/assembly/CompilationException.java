package com.gamevm.compiler.assembly;

import com.gamevm.compiler.parser.ASTNode;

public class CompilationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ASTNode node;
	
	public CompilationException(String msg, ASTNode node) {
		super(msg);
		this.node = node;
	}
	
	public ASTNode getNode() {
		return node;
	}

}
