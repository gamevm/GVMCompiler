package com.gamevm.ebc.compiler.grammar;

import java.util.Collection;
import java.util.Map;

public class Assignment implements Statement {

	private String lexpr;
	private Expression rexpr;
	
	public Assignment(String lexpr, Expression rexpr) {
		this.lexpr = lexpr;
		this.rexpr = rexpr;
	}
	
	public void execute(Map<String, Object> environment) {
		environment.put(lexpr, rexpr.evaluate(environment));
	}
	
	public String toString() {
		return String.format("%s = %s;", lexpr, rexpr);
	}

	@Override
	public Collection<Instruction> compile() throws CompilationException {
		Variable v = Variable.variables.get(lexpr);
		
		if (v == null)
			throw new CompilationException("Unknown variable " + lexpr);
			
		if (Type.matches(v.getType(), rexpr.inferType())) {
			Collection<Instruction> instr = rexpr.compile();
			instr.add(v.store());
			return instr;
		} else {
			throw new CompilationException("Invalid type of right hand side expression. Must be " + v.getType().getName());
		}
	}
	
	

}
