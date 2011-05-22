package com.gamevm.execution.ast.tree;

import java.util.Collection;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.execution.ast.Environment;

public class StaticMethodInvocation extends AbstractMethodInvocation {

	private static final long serialVersionUID = 1L;

	public StaticMethodInvocation(int classIndex,
			int methodIndex, Collection<Expression> parameters, ClassDeclaration parentClass) {
		super(classIndex, methodIndex, parameters, parentClass);
	}

	@Override
	protected Object callMethod(Object... parameters) throws InterruptedException {
		return Environment.getInstance().callStaticMethod(classIndex, methodIndex, parameters);
	}

}
