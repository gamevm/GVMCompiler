package com.gamevm.compiler.parser.old;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IfStatement implements Statement {
	
	private List<IfClause> clauses;
	
	public IfStatement(List<IfClause> clauses) {
		this.clauses = clauses;
	}
	

//	@Override
//	public void execute(Map<String, Object> environment) {
//		for (IfClause c : clauses) {
//			
//			if (c.condition == null) {
//				c.body.execute(environment);
//				return;
//			}
//			
//			boolean conditionIsTrue = (Boolean)c.condition.evaluate(environment);
//			
//			if (conditionIsTrue) {
//				c.body.execute(environment);
//				return;
//			}
//			
//			
//		}
//	}
	
	public String toString() {
		int i = 0;
		StringBuilder b = new StringBuilder();
		for (IfClause c : clauses) {
			if (i == 0) {
				b.append(String.format("if (%s) %s", c.condition, c.body));
			} else if (c.condition == null) {
				b.append(String.format("else %s", c.body));
			} else {
				b.append(String.format("else if (%s) %s", c.condition, c.body));
			}
			i++;
		}
		return b.toString();
	}


	@Override
	public Collection<Instruction> compile() throws CompilationException {
		Collection<Instruction> instr = new LinkedList<Instruction>();
		for (IfClause c : clauses) {
			instr.addAll(c.compile());
		}
		
		int size = instr.size() + clauses.size();
		int current = 0;
		instr.clear();
		for (IfClause c : clauses) {
			instr.addAll(c.compile());
			current = instr.size()+1;
			instr.add(new Instruction(Instruction.OP_JMP, (short)(size-current)));
		}
		
		
		return instr;
	}

}
