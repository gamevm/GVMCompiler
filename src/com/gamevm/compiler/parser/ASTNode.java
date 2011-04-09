package com.gamevm.compiler.parser;

public interface ASTNode {
	
	public static final String[] strings = new String[] {"TYPE_CLASS", "TYPE_FIELD", "TYPE_METHOD", "TYPE_WHILE_LOOP", "TYPE_FOR_LOOP", "TYPE_IF", "TYPE_ASSIGNMENT", "TYPE_RETURN", "TYPE_OPERATOR_PLUS", "TYPE_METHOD_CALL"};

	public static final int TYPE_CLASS = 0;
	public static final int TYPE_FIELD = 1;
	public static final int TYPE_METHOD = 2;
	public static final int TYPE_WHILE_LOOP = 3;
	public static final int TYPE_FOR_LOOP = 4;
	public static final int TYPE_IF = 5;
	public static final int TYPE_ASSIGNMENT = 6;
	public static final int TYPE_RETURN = 7;
	public static final int TYPE_OPERATOR_PLUS = 8;
	public static final int TYPE_METHOD_CALL = 9;
	
	
}
