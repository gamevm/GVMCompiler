package com.gamevm.execution.ast.tree;

import com.gamevm.utils.StringFormatter;

public class ExpressionStatement implements Statement {

	private Expression<?> e;
	
	public ExpressionStatement(Expression<?> e) {
		this.e = e;
	}
	
	@Override
	public String toString(int ident) {
		return String.format("%s%s;", StringFormatter.generateWhitespaces(ident), e.toString(0));
	}

	@Override
	public void execute() {
		e.evaluate();
	}

}
