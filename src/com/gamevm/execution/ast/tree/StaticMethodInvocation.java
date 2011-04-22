package com.gamevm.execution.ast.tree;

import java.util.Collection;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.execution.ast.Environment;

public class StaticMethodInvocation<R> extends AbstractMethodInvocation<R> {

	public StaticMethodInvocation(int classIndex,
			int methodIndex, Collection<Expression<?>> parameters, ClassDeclaration parentClass) {
		super(classIndex, methodIndex, parameters, parentClass);
	}

	@Override
	protected R callMethod(Object... parameters) throws InterruptedException {
		return Environment.getInstance().callStaticMethod(classIndex, methodIndex, parameters);
	}

}
