package com.gamevm.execution.ast.builtin;

import com.gamevm.execution.ast.NativeClassInstance;

public class SystemInstance extends NativeClassInstance {

	public SystemInstance() throws SecurityException, NoSuchMethodException {
		super("gc.System");
	}
	
	public static void print(StringInstance str) {
		System.out.println(str);
	}

}
