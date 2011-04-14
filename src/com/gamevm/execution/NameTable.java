package com.gamevm.execution;

import com.gamevm.compiler.assembly.Field;
import com.gamevm.compiler.assembly.Method;

public interface NameTable {

	public String getLocalVariableName(int index);
	
	public Method getMethod(int classIndex, int methodIndex);
	
	public String getClassName(int classIndex);
	
	public Field getField(int classIndex, int fieldIndex);
	
}
