package com.gamevm.compiler.translator.ast;

import com.gamevm.execution.ast.tree.NotAddressable;
import com.gamevm.utils.StringFormatter;

public class QualifiedNameExpression extends NotAddressable<String> {

	private StringBuilder name;
	
	public QualifiedNameExpression(String start) {
		name = new StringBuilder();
		name.append(start);
	}
	
	public void appendName(String s) {
		name.append('.');
		name.append(s);
	}
	
	@Override
	public String evaluate() throws InterruptedException {
		return name.toString();
	}
	
	@Override
	public String toString(int ident) {
		return String.format("%s", name);
	}


}
