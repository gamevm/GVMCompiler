package com.gamevm.execution.ast.builtin;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.Field;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.compiler.assembly.Modifier;
import com.gamevm.compiler.assembly.Type;
import com.gamevm.execution.ast.ClassInstance;
import com.gamevm.execution.ast.Environment;
import com.gamevm.execution.ast.LoadedClass;
import com.gamevm.execution.ast.tree.Statement;

public class StringClass extends LoadedClass {

	static final Method LENGTH = new Method(Modifier.PUBLIC, Type.INT, "length");
	static final int METHOD_LENGTH = 0;
	
	public static final ClassDeclaration DECLARATION = new ClassDeclaration(Modifier.getFlag(Modifier.PUBLIC, false,
			true), "gc.String", new Field[0], new Method[] { LENGTH }, new Type[0]);
	
	public static final LoadedClass CLASS = new StringClass();
	
	public StringClass() {
		super(new ClassDefinition<Statement>(Environment.FILE_HEADER, DECLARATION, null, null, null), -1);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T callNative(int index, ClassInstance thisClass, Object... parameters) {
		switch (index) {
		case METHOD_LENGTH:
			return (T)Integer.valueOf(((StringInstance)thisClass).s.length());
		}
		return null;
	}
	
	@Override
	public boolean isNative() {
		return true;
	}

}
