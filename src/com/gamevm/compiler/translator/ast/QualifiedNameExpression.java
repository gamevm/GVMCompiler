package com.gamevm.compiler.translator.ast;


public class QualifiedNameExpression {

	private StringBuilder name;
	
	public QualifiedNameExpression(String start) {
		name = new StringBuilder();
		name.append(start);
	}
	
	public void appendName(String s) {
		name.append('.');
		name.append(s);
	}
	
	public String evaluate() {
		return name.toString();
	}


}
