package com.gamevm.execution;

import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.code.Code;
import com.gamevm.compiler.assembly.loader.Loader;
import com.gamevm.execution.ast.DebugHandler;

public abstract class Interpreter<C extends Code> {

	protected RuntimeEnvironment system;
	
	public Interpreter(RuntimeEnvironment system) {
		this.system = system;
	}
	
	public abstract void setDebugMode(boolean on, DebugHandler handler);
	
	public abstract void continueExecution();
	
	public abstract void abortExecution();
	
	public abstract int execute(ClassDefinition<C> mainClass, String[] args, InterpretationListener l, Loader classLoader) throws Exception;
	
}
