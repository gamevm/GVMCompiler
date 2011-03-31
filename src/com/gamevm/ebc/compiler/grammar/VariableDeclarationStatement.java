package com.gamevm.ebc.compiler.grammar;

import java.util.Collection;
import java.util.LinkedList;

public class VariableDeclarationStatement implements Statement {

	private Class<?> type;
	private String name;
	
	public VariableDeclarationStatement(Class<?> type, String name) {
		this.type = type;
		this.name = name;
	}
	
	@Override
	public Collection<Instruction> compile() throws CompilationException {
		Variable.declareVariable(type, name);
		return new LinkedList<Instruction>();
	}
	
	@Override
	public String toString() {
		return String.format("%s %s", type.getName(), name);
	}

}
