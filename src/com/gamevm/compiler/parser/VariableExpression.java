package com.gamevm.compiler.parser;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

public class VariableExpression implements Expression {

	private String name;
	
	public VariableExpression(String name) {
		this.name = name;
	}
	
	@Override
	public Object evaluate(Map<String, Object> environment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> inferType() throws CompilationException {
		Variable v = Variable.variables.get(name);
		
		if (v == null)
			throw new CompilationException("Unknown variable " + name);
		
		return v.getType();
	}

	@Override
	public Collection<Instruction> compile() throws CompilationException {
		Collection<Instruction> instr = new LinkedList<Instruction>();
		Variable v = Variable.variables.get(name);
		if (v == null)
			throw new CompilationException("Unknown variable " + name);
		instr.add(v.load());
		return instr;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
