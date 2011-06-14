package com.gamevm.compiler.experiments.parser;

import java.util.Collection;
import java.util.Map;

public interface Statement {
	
	//public void execute(Map<String, Object> environment);
	
	public Collection<Instruction> compile() throws CompilationException;

}
