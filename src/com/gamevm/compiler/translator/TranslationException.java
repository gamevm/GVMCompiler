package com.gamevm.compiler.translator;

import com.gamevm.compiler.parser.ASTNode;

public class TranslationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ASTNode node;
	
	public TranslationException(String message, ASTNode node) {
		super(message);
		this.node = node;
	}
	
	public TranslationException(String message, Throwable cause, ASTNode node) {
		super(message, cause);
		this.node = node;
	}
	
	public ASTNode getNode() {
		return node;
	}

}
