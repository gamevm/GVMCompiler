package com.gamevm.execution.ast.tree;

import com.gamevm.utils.StringFormatter;

public class WhileStatement extends Statement {

	private static final long serialVersionUID = 1L;
	private Expression condition;
	private Statement body;

	public WhileStatement(Expression condition, Statement body) {
		this.condition = condition;
		this.body = body;
	}

	@Override
	public String toString(int ident) {
		return String.format("%swhile(%s)\n%s",
				StringFormatter.generateWhitespaces(ident),
				condition.toString(0), body.toString(ident+2));
	}

	@Override
	public void execute() throws InterruptedException {
		super.execute();
		while ((Boolean)condition.evaluate()) {
			body.execute();
		}
	}
	
	@Override
	public String toString() {
		return toString(0);
	}
	
	@Override
	public int getMaxLocals() {
		return body.getMaxLocals();
	}

}
