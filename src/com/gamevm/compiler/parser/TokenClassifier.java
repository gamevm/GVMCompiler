package com.gamevm.compiler.parser;

import java.util.BitSet;

import org.antlr.runtime.Token;

public class TokenClassifier {
	
	private BitSet keywords;
	
	public TokenClassifier() {
		keywords = new BitSet(256);
		initializeKeywords();
	}
	
	private void initializeKeywords() {
		keywords.set(GCASTLexer.PACKAGE);
		keywords.set(GCASTLexer.IMPORT);
		keywords.set(GCASTLexer.CLASS);
		keywords.set(GCASTLexer.EXTENDS);
		keywords.set(GCASTLexer.IMPLEMENTS);
		keywords.set(GCASTLexer.STATIC);
		keywords.set(GCASTLexer.FINAL);
		keywords.set(GCASTLexer.PUBLIC);
		keywords.set(GCASTLexer.PROTECTED);
		keywords.set(GCASTLexer.PRIVATE);
		keywords.set(GCASTLexer.IF);
		keywords.set(GCASTLexer.ELSE);
		keywords.set(GCASTLexer.FOR);
		keywords.set(GCASTLexer.WHILE);
		keywords.set(GCASTLexer.RETURN);
		keywords.set(GCASTLexer.NEW);
		keywords.set(GCASTLexer.BYTE);
		keywords.set(GCASTLexer.SHORT);
		keywords.set(GCASTLexer.INT);
		keywords.set(GCASTLexer.LONG);
		keywords.set(GCASTLexer.FLOAT);
		keywords.set(GCASTLexer.DOUBLE);
		keywords.set(GCASTLexer.CHAR);
		keywords.set(GCASTLexer.BOOLEAN);
		keywords.set(GCASTLexer.VOID);
	}
	
	public boolean isKeyword(Token t) {
		return keywords.get(t.getType());
	}

}
