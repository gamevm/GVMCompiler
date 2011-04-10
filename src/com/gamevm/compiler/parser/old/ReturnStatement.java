package com.gamevm.compiler.parser.old;

import java.util.Collection;
import java.util.Map;

public class ReturnStatement implements Statement {

	private Expression expression;
	
	public ReturnStatement(Expression expression) {
		this.expression = expression;
	}

//	@Override
//	public void execute(Map<String, Object> environment) {
//		environment.put("__result__", expression.evaluate(environment));
//	}
	
	public String toString() {
		return String.format("return %s;", expression);
	}

	@Override
	public Collection<Instruction> compile() throws CompilationException {
		throw new CompilationException("Statement not supported");
	}
	
}
