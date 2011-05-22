package com.gamevm.execution.ast.builtin;

import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.code.ExecutableTreeCode;
import com.gamevm.compiler.assembly.runtime.RuntimeClasses;
import com.gamevm.execution.ast.ClassInstance;
import com.gamevm.execution.ast.Environment;
import com.gamevm.execution.ast.LoadedClass;

public class SystemClass extends LoadedClass {

	public static final LoadedClass CLASS = new SystemClass();
	
	public SystemClass() {
		super(new ClassDefinition<ExecutableTreeCode>(Environment.FILE_HEADER, RuntimeClasses.DECLARATION_SYSTEM, null, null, null), -1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T callNative(int index, ClassInstance thisClass, Object... parameters) {
		switch (index) {
		case RuntimeClasses.METHOD_SYSTEM_PRINT:
			System.out.print(((StringInstance)parameters[0]).getInternal());
			break;
		case RuntimeClasses.METHOD_SYSTEM_GET_CHARACTER_VALUE:
			return (T)Integer.valueOf(((Character)parameters[0]).charValue());
		}
		return null;
	}

	@Override
	public boolean isNative() {
		return true;
	}

}
