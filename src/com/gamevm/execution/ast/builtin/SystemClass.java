package com.gamevm.execution.ast.builtin;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.Field;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.compiler.assembly.Modifier;
import com.gamevm.compiler.assembly.Variable;
import com.gamevm.compiler.assembly.runtime.RuntimeClasses;
import com.gamevm.execution.ast.ClassInstance;
import com.gamevm.execution.ast.Environment;
import com.gamevm.execution.ast.LoadedClass;
import com.gamevm.execution.ast.tree.Statement;

public class SystemClass extends LoadedClass {

	public static final LoadedClass CLASS = new SystemClass();
	
	public SystemClass() {
		super(new ClassDefinition<Statement>(Environment.FILE_HEADER, RuntimeClasses.DECLARATION_SYSTEM, null, null, null), -1);
	}

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
