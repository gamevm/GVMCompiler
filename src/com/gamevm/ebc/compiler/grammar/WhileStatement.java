package com.gamevm.ebc.compiler.grammar;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WhileStatement implements Statement {
	
	private Expression loopCondition;
	private Body body;
	
	public WhileStatement(Expression loopCondition, List<Statement> body) {
		this.loopCondition = loopCondition;
		this.body = new Body(body);
	}
	
	private boolean loopCondition(Map<String, Object> environment) {
		return (Boolean)loopCondition.evaluate(environment);
	}

//	@Override
//	public void execute(Map<String, Object> environment) {
//		while (loopCondition(environment)) {
//			body.execute(environment);
//		}
//	}
	
	public String toString() {
		return String.format("while (%s) %s", loopCondition, body);
	}

	@Override
	public Collection<Instruction> compile() throws CompilationException {
		Collection<Instruction> instr = new LinkedList<Instruction>();
		instr.addAll(loopCondition.compile());
		
		Collection<Instruction> loopBody = body.compile();
		
		instr.add(new Instruction(Instruction.OP_IEQN, (short)(loopBody.size()+1)));
		instr.addAll(loopBody);
		instr.add(new Instruction(Instruction.OP_JMP, (short)(-instr.size()-1)));
		
		
		return instr;
	}

}
