package com.gamevm.execution;

import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.Instruction;

public abstract class Interpreter<I extends Instruction> {

	protected RuntimeEnvironment system;
	
	public Interpreter(RuntimeEnvironment system) {
		this.system = system;
	}
	
	public abstract int execute(ClassDefinition<I> mainClass, String[] args) throws Exception;
	
}
