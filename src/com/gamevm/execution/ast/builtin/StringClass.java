package com.gamevm.execution.ast.builtin;

import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.code.ExecutableTreeCode;
import com.gamevm.compiler.assembly.runtime.RuntimeClasses;
import com.gamevm.execution.ast.ClassInstance;
import com.gamevm.execution.ast.Environment;
import com.gamevm.execution.ast.LoadedClass;

public class StringClass extends LoadedClass {
	
	public static final LoadedClass CLASS = new StringClass();
	
	public StringClass() {
		super(new ClassDefinition<ExecutableTreeCode>(Environment.FILE_HEADER, RuntimeClasses.DECLARATION_STRING, null, null, null), -1);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T callNative(int index, ClassInstance thisClass, Object... parameters) {
		String s = ((StringInstance)thisClass).s;
		switch (index) {
		case RuntimeClasses.METHOD_STRING_LENGTH:
			return (T)Integer.valueOf(s.length());
		case RuntimeClasses.METHOD_STRING_TOCHARARRAY:
			char[] carr = s.toCharArray();
			Character[] ocarr = new Character[carr.length];
			for (int i = 0; i < carr.length; i++) {
				ocarr[i] = carr[i];
			}
			return (T)new ArrayInstance(ocarr);
		}
		return null;
	}
	
	@Override
	public boolean isNative() {
		return true;
	}

}
