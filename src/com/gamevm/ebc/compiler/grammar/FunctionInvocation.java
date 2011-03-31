package com.gamevm.ebc.compiler.grammar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FunctionInvocation implements Expression, Statement {
	
	private String name;
	private List<Expression> parameters;
	
	public FunctionInvocation(String name, List<Expression> parameters) {
		this.name = name;
		this.parameters = parameters;
	}

	@Override
	public Object evaluate(Map<String, Object> environment) {
		
		List<Object> parameterValues = new ArrayList<Object>();
		for (Expression e : parameters) {
			parameterValues.add(e.evaluate(environment));
		}
		
		return null; //FunctionDeclaration.functions.get(name).execute(parameterValues, environment);
	}

	public void execute(Map<String, Object> environment) {
		evaluate(environment);
	}
	
	public String toString() {
		return String.format("%s(%s)", name, parameters);
	}

	@Override
	public Class<?> inferType() throws CompilationException {
		return FunctionDeclaration.functions.get(name).getReturnType();
	}

	@Override
	public Collection<Instruction> compile() throws CompilationException {
		throw new CompilationException("Function Invocation not supported");
	}

}
