package com.gamevm.compiler.parser.old;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

public class EmptyStatement implements Statement {

	public void execute(Map<String, Object> environment) {
	}
	
	public String toString() {
		return ";";
	}

	@Override
	public Collection<Instruction> compile() throws CompilationException {
		return new LinkedList<Instruction>();
	}

}
