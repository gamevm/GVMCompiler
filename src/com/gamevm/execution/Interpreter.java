package com.gamevm.execution;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;

public abstract class Interpreter {

	protected PrintStream out;
	protected PrintStream err;
	protected InputStream in;
	
	public Interpreter(PrintStream out, PrintStream err, InputStream in) {
		this.out = out;
		this.err = err;
		this.in = in;
	}
	
	public abstract void execute(Reader input, String[] args) throws IOException;
	
}
