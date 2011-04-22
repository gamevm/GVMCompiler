package com.gamevm.compiler.parser.old;

import java.util.Collection;
import java.util.LinkedList;

public class PrintStatement implements Statement {
	
	private Expression argument;
	
	public PrintStatement(Expression argument) {
		this.argument = argument;
	}

//	@Override
//	public void execute(Map<String, Object> environment) {
//		System.out.println(argument.evaluate(environment));
//	}
	
	public String toString() {
		return String.format("print(%s)", argument);
	}

	@Override
	public Collection<Instruction> compile() throws CompilationException {
		Collection<Instruction> instr = new LinkedList<Instruction>();
		instr.addAll(argument.compile());
		
		if (!argument.inferType().equals(String.class)) {
			instr.add(new Instruction(Instruction.OP_SCONV, (short)0));
		}
		
		instr.add(new Instruction(Instruction.OP_PRINT, (short)0));
		return instr;
	}

}
