package com.gamevm.compiler.parser.old;

import java.util.Collection;
import java.util.Map;

public class NumericNegation implements Expression {

	private Expression e;
	
	public NumericNegation(Expression e) {
		this.e = e;
	}

	@Override
	public Object evaluate(Map<String, Object> environment) {
		return -(Long)e.evaluate(environment);
	}
	
	public String toString() {
		return String.format("(-%s)", e);
	}

	@Override
	public Class<?> inferType() throws CompilationException {
		return e.inferType();
	}

	@Override
	public Collection<Instruction> compile() throws CompilationException {
		throw new CompilationException("Statement not supported");
	}
	
}
