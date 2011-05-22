package com.gamevm.execution.ast.tree;

import com.gamevm.utils.StringFormatter;

public class IfStatement extends Statement {

	private static final long serialVersionUID = 1L;
	private Expression condition;
	private Statement body;
	private Statement elseStatement;

	public IfStatement(Expression condition, Statement body,
			Statement elseStatement) {
		super();
		this.condition = condition;
		this.body = body;
		this.elseStatement = elseStatement;
	}

	@Override
	public String toString(int ident) {
		StringBuilder b = new StringBuilder();
		String ws = StringFormatter.generateWhitespaces(ident);
		b.append(ws);
		b.append("if (");
		b.append(condition.toString(0));
		b.append(")\n");
		b.append(body.toString(ident+2));
		b.append("\n");
		b.append(ws);
		b.append("else");
		b.append(elseStatement.toString(ident+2));
		return b.toString();
	}

	@Override
	public void execute() throws InterruptedException {
		super.execute();
		if ((Boolean)condition.evaluate()) {
			body.execute();
		} else {
		elseStatement.execute();
		}
	}
	
	@Override
	public String toString() {
		return toString(0);
	}

}
