package com.gamevm.execution.ast.tree;

import com.gamevm.utils.StringFormatter;

public class WhileStatement implements Statement {

	private Expression<Boolean> condition;
	private Block body;

	public WhileStatement(Expression<Boolean> condition, Block body) {
		this.condition = condition;
		this.body = body;
	}

	@Override
	public String toString(int ident) {
		return String.format("%swhile(%s)\n%s",
				StringFormatter.generateWhitespaces(ident),
				condition.toString(0), body.toString(ident));
	}

	@Override
	public void execute() {
		while (condition.evaluate()) {
			body.execute();
		}
	}

}
