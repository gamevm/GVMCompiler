package com.gamevm.execution;

import java.io.InputStream;
import java.io.PrintStream;

public class RuntimeEnvironment {
	
	public PrintStream out;
	public PrintStream err;
	public InputStream in;
	
	public RuntimeEnvironment(PrintStream out, PrintStream err, InputStream in) {
		super();
		this.out = out;
		this.err = err;
		this.in = in;
	}
	
	

}
