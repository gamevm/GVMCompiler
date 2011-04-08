package com.gamevm.compiler.parser;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class IfClause {
	
	Expression condition;
	Body body;
	
	public IfClause(Expression condition, List<Statement> body) {
		super();
		this.condition = condition;
		this.body = new Body(body);
	}
	
	public Collection<Instruction> compile() throws CompilationException {
		if (condition != null) {
			Collection<Instruction> instr = new LinkedList<Instruction>();
			instr.addAll(condition.compile());
			Collection<Instruction> bodyInstr = body.compile();
			instr.add(new Instruction(Instruction.OP_IEQN, (short)(bodyInstr.size()+1)));
			instr.addAll(bodyInstr);
			return instr;
		} else {
			return body.compile();
		}
	}

}
