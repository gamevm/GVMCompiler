package com.gamevm.execution.ast.tree;

import com.gamevm.utils.StringFormatter;

public class ExpressionStatement extends Statement {

	private static final long serialVersionUID = 1L;
	private Expression e;
	
	public ExpressionStatement(Expression e) {
		this.e = e;
	}
	
	@Override
	public String toString(int ident) {
		return String.format("%s%s;", StringFormatter.generateWhitespaces(ident), e.toString(0));
	}

	@Override
	public void execute() throws InterruptedException {
		super.execute();
		e.evaluate();
	}
	
	@Override
	public String toString() {
		return toString(0);
	}

}
