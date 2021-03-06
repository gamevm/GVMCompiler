package com.gamevm.compiler.experiments.parser;

import java.util.Collection;
import java.util.Map;

public interface Expression {

	public Object evaluate(Map<String, Object> environment);
	
	public Class<?> inferType() throws CompilationException;
	
	public Collection<Instruction> compile() throws CompilationException;

}
