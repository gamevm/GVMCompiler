package com.gamevm.execution.ast.tree;

import java.util.Collection;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.execution.ast.ClassInstance;
import com.gamevm.execution.ast.Environment;
import com.gamevm.utils.StringFormatter;

public class OpNew extends AbstractMethodInvocation<ClassInstance> {

	public OpNew(int classIndex, int constructorIndex,
			Collection<Expression<?>> parameters, ClassDeclaration classType) {
		super(classIndex, constructorIndex, parameters, classType);
	}

	@Override
	public String toString(int ident) {
		return String.format("new %s(%s)", parentClassName, StringFormatter.printIterable(parameters, ", "));
	}

	@Override
	protected ClassInstance callMethod(Object... parameters) throws InterruptedException {
		return Environment.getInstance().newInstance(classIndex, methodIndex, parameters);
	}

}
