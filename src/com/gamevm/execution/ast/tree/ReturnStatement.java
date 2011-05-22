package com.gamevm.execution.ast.tree;

import com.gamevm.execution.ast.Environment;
import com.gamevm.utils.StringFormatter;

public class ReturnStatement extends Statement {

	private static final long serialVersionUID = 1L;
	private Expression expression;
	
	public ReturnStatement(Expression e) {
		this.expression = e;
	}
	
	@Override
	public String toString(int ident) {
		return StringFormatter.generateWhitespaces(ident) + "return " + expression.toString(0) + ";";
	}

	@Override
	public void execute() throws InterruptedException {
		super.execute();
		Environment.getInstance().writeReturnRegister(expression.evaluate());
		throw new ReturnException();
	}
	
	@Override
	public String toString() {
		return toString(0);
	}

}
