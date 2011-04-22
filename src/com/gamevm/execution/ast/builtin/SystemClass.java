package com.gamevm.execution.ast.builtin;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.Field;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.compiler.assembly.Modifier;
import com.gamevm.compiler.assembly.Type;
import com.gamevm.compiler.assembly.Variable;
import com.gamevm.execution.ast.ClassInstance;
import com.gamevm.execution.ast.Environment;
import com.gamevm.execution.ast.LoadedClass;
import com.gamevm.execution.ast.tree.Statement;

public class SystemClass extends LoadedClass {

	static final Method PRINT = new Method(Modifier.getFlag(Modifier.PUBLIC, true, true), Type.VOID, "print",
			new Variable(Type.getType("gc.String"), "arg"));
	static final int METHOD_PRINT = 0;

	public static final ClassDeclaration DECLARATION = new ClassDeclaration(Modifier.getFlag(Modifier.PUBLIC, false,
			true), "gc.System", new Field[0], new Method[] { PRINT }, new Type[] { Type.getType("gc.String") });

	public static final LoadedClass CLASS = new SystemClass();
	
	public SystemClass() {
		super(new ClassDefinition<Statement>(Environment.FILE_HEADER, DECLARATION, null, null, null), -1);
	}

	@Override
	public <T> T callNative(int index, ClassInstance thisClass, Object... parameters) {
		switch (index) {
		case METHOD_PRINT:
			System.out.print(((StringInstance)parameters[0]).getInternal());
		}
		return null;
	}

	@Override
	public boolean isNative() {
		return true;
	}

}
