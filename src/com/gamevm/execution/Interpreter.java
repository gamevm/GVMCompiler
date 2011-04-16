package com.gamevm.execution;

import java.io.File;

import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.Instruction;
import com.gamevm.execution.ast.DebugHandler;

public abstract class Interpreter<I extends Instruction> {

	protected RuntimeEnvironment system;
	
	public Interpreter(RuntimeEnvironment system) {
		this.system = system;
	}
	
	public abstract void setDebugMode(boolean on, DebugHandler handler);
	
	public abstract void continueExecution();
	
	public abstract void abortExecution();
	
	public abstract int execute(ClassDefinition<I> mainClass, String[] args, InterpretationListener l, File... classPath) throws Exception;
	
}
