package com.gamevm.compiler.parser;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ForStatement implements Statement {
	
	private Body body;
	private Statement initializer;
	private Expression condition;
	private List<Statement> increment;
	
	public ForStatement(List<Statement> body, Statement initializier, Expression condition, List<Statement> increment) {
		this.body = new Body(body);
		this.initializer = initializier;
		this.condition = condition;
		this.increment = increment;
	}
	
	private boolean loopCondition(Map<String, Object> environment) {
		return (Boolean)condition.evaluate(environment);
	}

//	@Override
//	public void execute(Map<String, Object> environment) {
//		initializer.execute(environment);
//		
//		while (loopCondition(environment)) {
//			
//			body.execute(environment);
//			
//			for (Statement s : increment) {
//				s.execute(environment);
//			}
//			
//		}
//		
//	}
	
	public String toString() {
		
		StringBuilder b = new StringBuilder();
		for (Statement s : increment) {
			b.append(s);
		}
		
		return String.format("for (%s; %s; %s) %s", initializer, condition, b.toString(), body);
	}

	@Override
	public Collection<Instruction> compile() throws CompilationException {
		Collection<Instruction> instr = new LinkedList<Instruction>();
		instr.addAll(initializer.compile());
	
		Collection<Instruction> loopCode = new LinkedList<Instruction>();
		loopCode.addAll(condition.compile());
		
		Collection<Instruction> innerLoopCode = new LinkedList<Instruction>();
		innerLoopCode.addAll(body.compile());
		for (Statement s : increment) {
			innerLoopCode.addAll(s.compile());
		}
		innerLoopCode.add(new Instruction(Instruction.OP_JMP, (short)-(innerLoopCode.size() + loopCode.size() + 2)));
		
		loopCode.add(new Instruction(Instruction.OP_IEQN, (short)innerLoopCode.size()));
		
		loopCode.addAll(innerLoopCode);
		instr.addAll(loopCode);
		
		return instr;
	}

}
