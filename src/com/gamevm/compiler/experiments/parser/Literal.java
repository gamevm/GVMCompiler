package com.gamevm.compiler.experiments.parser;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

public class Literal<T> implements Expression {

	private T value;
	
	public Literal(T value) {
		this.value = value;
	}

	@Override
	public Object evaluate(Map<String, Object> environment) {
		return value;
	}
	
	public String toString() {
		if (value instanceof String) {
			return "\"" + value + "\"";
		} else if (value instanceof Character) {
			return "'" + value + "'";
		} else {
			return value.toString();
		}
	}

	@Override
	public Class<?> inferType() throws CompilationException {
		return value.getClass();
	}

	@Override
	public Collection<Instruction> compile() throws CompilationException {
		Collection<Instruction> instr = new LinkedList<Instruction>();
		if (value instanceof Number)
			instr.add(new Instruction(Instruction.OP_ICONST, ((Number)value).shortValue()));
		else if (value instanceof String) {
			Instruction.stringLiterals.push((String)value);
			instr.add(new Instruction(Instruction.OP_SCONST, (short)(Instruction.stringLiterals.size()-1)));
		} else
			throw new CompilationException("Statement not supported.");
		
		return instr;
	}
	
}
