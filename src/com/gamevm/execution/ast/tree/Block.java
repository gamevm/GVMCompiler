package com.gamevm.execution.ast.tree;

import java.util.Collection;

public class Block extends Statement {

	private static final long serialVersionUID = 1L;
	private Collection<Statement> body;
	
	public Block(Collection<Statement> body) {
		this.body = body;
	}
	
	@Override
	public String toString(int ident) {
		StringBuilder b = new StringBuilder();
		for (Statement s : body) {
			b.append(s.toString(ident));
			b.append('\n');
		}
		return b.toString();
	}

	@Override
	public void execute() throws InterruptedException {
		super.execute();
		for (Statement s : body) {
			s.execute();
		}
	}
	
	@Override
	public String toString() {
		return toString(0);
	}

}
