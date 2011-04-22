package com.gamevm.execution.ast.tree;

import java.util.Collection;

import com.gamevm.compiler.assembly.InstructionVisitor;
import com.gamevm.execution.ast.Environment;
import com.gamevm.utils.StringFormatter;

public class Block extends Statement {

	private Collection<Statement> body;
	
	public Block(Collection<Statement> body) {
		this.body = body;
	}
	
	@Override
	public String toString(int ident) {
		StringBuilder b = new StringBuilder();
		String ws = StringFormatter.generateWhitespaces(ident-2);
		b.append(ws);
		b.append("{\n");
		for (Statement s : body) {
			s.toString(ident);
			b.append('\n');
		}
		b.append(ws);
		b.append("}");
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
