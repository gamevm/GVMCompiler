package com.gamevm.execution.ast.tree;

import java.util.Collection;

import com.gamevm.execution.ast.Environment;
import com.gamevm.utils.StringFormatter;

public class ForStatement implements Statement {

	private Statement initialization;
	private Expression<Boolean> condition;
	private Collection<Statement> postActions;
	private Collection<Statement> body;
	
	public ForStatement(Statement initialization,
			Expression<Boolean> condition, Collection<Statement> postActions,
			Collection<Statement> body) {
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
		b.append(ws);
		b.append("{");
		b.append(StringFormatter.printIterable(body, "\n" + ws + "  "));
		b.append(ws);
		b.append("}");
		return b.toString();
	}

	@Override
	public void execute() {
		Environment.pushFrame();
		initialization.execute();
		while (condition.evaluate()) {
			for (Statement s : body) {
				s.execute();
			}
			for (Statement s : postActions) {
				s.execute();
			}
		}
		Environment.popFrame();
	}

}
