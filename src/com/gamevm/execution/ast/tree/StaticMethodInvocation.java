package com.gamevm.execution.ast.tree;

import java.util.Collection;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.execution.ast.Environment;

public class StaticMethodInvocation<R> extends AbstractMethodInvocation<R, ClassDefinition<Statement>> {

	public StaticMethodInvocation(int classIndex,
			int methodIndex, Collection<Expression<?>> parameters, ClassDeclaration parentClass) {
		super(classIndex, methodIndex, parameters, parentClass);
	}

	@Override
	protected R callMethod(Object... parameters) {
		return Environment.callStaticMethod(classIndex, methodIndex, parameters);
	}

}
