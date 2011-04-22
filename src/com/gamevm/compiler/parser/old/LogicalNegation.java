package com.gamevm.compiler.parser.old;

import java.util.Collection;
import java.util.Map;

public class LogicalNegation implements Expression {
	
	private Expression e;
	
	public LogicalNegation(Expression e) {
		this.e = e;
	}

	@Override
	public Object evaluate(Map<String, Object> environment) {
		return !(Boolean)e.evaluate(environment);
	}
	
	public String toString() {
		return String.format("(!%s)", e);
	}

	@Override
	public Class<?> inferType() throws CompilationException {
		return Boolean.class;
	}

	@Override
	public Collection<Instruction> compile() throws CompilationException {
		throw new CompilationException("Statement not supported");
	}

}
