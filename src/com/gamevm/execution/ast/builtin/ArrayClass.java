package com.gamevm.execution.ast.builtin;

import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.code.ExecutableTreeCode;
import com.gamevm.compiler.assembly.runtime.RuntimeClasses;
import com.gamevm.compiler.translator.ast.ClassSymbol;
import com.gamevm.execution.ast.Environment;
import com.gamevm.execution.ast.LoadedClass;

public class ArrayClass extends LoadedClass {
	
	public static final ArrayClass CLASS = new ArrayClass();

	public ArrayClass() {
		super(new ClassDefinition<ExecutableTreeCode>(Environment.FILE_HEADER, RuntimeClasses.DECLARATION_ARRAY, null, null, null), ClassSymbol.ARRAY_MASK);
	}

	@Override
	public boolean isNative() {
		return true;
	}
}
