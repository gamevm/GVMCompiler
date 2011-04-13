package com.gamevm.execution.ast.tree;

import java.util.Collection;

import com.gamevm.utils.StringFormatter;

public class IfStatement implements Statement {

	private Expression<Boolean> condition;
	private Block body;
	private Collection<IfStatement> elses;

	public IfStatement(Expression<Boolean> condition, Block body,
			Collection<IfStatement> elses) {
		super();
		this.condition = condition;
		this.body = body;
		this.elses = elses;
	}

	@Override
	public String toString(int ident) {
		StringBuilder b = new StringBuilder();
		String ws = StringFormatter.generateWhitespaces(ident);
		b.append(ws);
		b.append("if (");
		b.append(condition.toString(0));
		b.append(")\n");
		b.append(body.toString(ident));
		for (IfStatement s : elses) {
			b.append(s.toString(ident));
		}
		return b.toString();
	}

	@Override
	public void execute() {
		if (condition.evaluate()) {
			body.execute();
		} else {
			for (IfStatement s : elses) {
				if (s.condition == null || s.condition.evaluate()) {
					s.body.execute();
					return;
				}
			}
		}
	}

}
