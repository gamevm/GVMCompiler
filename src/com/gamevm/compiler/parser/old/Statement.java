package com.gamevm.compiler.parser.old;

import java.util.Collection;
import java.util.Map;

public interface Statement {
	
	//public void execute(Map<String, Object> environment);
	
	public Collection<Instruction> compile() throws CompilationException;

}
