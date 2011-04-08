package com.gamevm.compiler.parser;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Body {
	
	private List<Statement> statements;
	
	public Body(List<Statement> statements) {
		this.statements = statements;
	}
	
//	public void execute(Map<String, Object> env) {
//		for (Statement s : statements) {
//			s.execute(env);
//		}
//	}
	
	public Collection<Instruction> compile() throws CompilationException {
		Iterator<Statement> i = statements.iterator();
		Collection<Instruction> instr = i.next().compile();
		while (i.hasNext()) {
			instr.addAll(i.next().compile());
		}
		return instr;
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("{\n");
		for (Statement s : statements) {
			b.append(s);
			b.append("\n");
		}
		b.append("}");
		return b.toString();
	}

}
