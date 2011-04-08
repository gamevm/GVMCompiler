package com.gamevm.compiler.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionDeclaration implements Statement {
	
	private String name;
	private Class<?> returnType;
	private Body body;
	private List<String> parameterNames;
	
	static Map<String, FunctionDeclaration> functions = new HashMap<String, FunctionDeclaration>();
	
	public FunctionDeclaration(Class<?> returnType, String name, List<String> parameterNames, List<Statement> body) {
		this.returnType = returnType;
		this.name = name;
		this.parameterNames = parameterNames;
		this.body = new Body(body);
	}
	
//	public Object execute(List<Object> parameters, Map<String, Object> environment) {
//		
//		List<Object> cached = new ArrayList<Object>();
//		
//		for (int i = 0; i < parameterNames.size(); i++) {
//			cached.add(environment.put(parameterNames.get(i), parameters.get(i)));
//		}
//
//		body.execute(environment);
//		
//		for (int i = 0; i < parameterNames.size(); i++) {
//			environment.put(parameterNames.get(i), cached.get(i));			
//		}
//		
//		return environment.remove("__result__");
//		
//	}

//	@Override
//	public void execute(Map<String, Object> environment) {
//		functions.put(name, this);
//	}
	
	public String toString() {
		return String.format("%s(%s) %s", name, parameterNames, body);
	}
	
	@Override
	public Collection<Instruction> compile() throws CompilationException {
		throw new CompilationException("Function Decl not supported");
	}
	
	public Class<?> getReturnType() {
		return returnType;
	}
	
	public String getName() {
		return name;
	}

}
