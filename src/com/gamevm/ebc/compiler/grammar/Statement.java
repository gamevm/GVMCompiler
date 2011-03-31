package com.gamevm.ebc.compiler.grammar;

import java.util.Collection;
import java.util.Map;

public interface Statement {
	
	//public void execute(Map<String, Object> environment);
	
	public Collection<Instruction> compile() throws CompilationException;

}
