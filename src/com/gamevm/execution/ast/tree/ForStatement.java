package com.gamevm.execution.ast.tree;

import java.util.Collection;

import com.gamevm.utils.StringFormatter;

public class ForStatement extends Statement {

	private static final long serialVersionUID = 1L;
	private Statement initialization;
	private Expression condition;
	private Collection<Statement> postActions;
	private Statement body;
	
	public ForStatement(Statement initialization,
			Expression condition, Collection<Statement> postActions,
			Statement body) {
		this.initialization = initialization;
		this.condition = condition;
		this.postActions = postActions;
		this.body = body;
	}

	@Override
	public String toString(int ident) {
		StringBuilder b = new StringBuilder();
		String ws = StringFormatter.generateWhitespaces(ident);
		b.append(ws);
		b.append("for (");
		b.append(initialization.toString(0));
		b.append(condition.toString(0));
		b.append(";");
		b.append(StringFormatter.printIterable(postActions, ""));
		b.append(")\n");
		b.append(body.toString(ident+2));
		return b.toString();
	}

	@Override
	public void execute() throws InterruptedException {
		super.execute();
		initialization.execute();
		while ((Boolean)condition.evaluate()) {
			body.execute();
			for (Statement s : postActions) {
				s.execute();
			}
		}
	}

	@Override
	public String toString() {
		return toString(0);
	}
}
