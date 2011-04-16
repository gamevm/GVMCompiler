package com.gamevm.execution.ast.tree;

import java.util.Collection;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.execution.ast.ClassInstance;
import com.gamevm.execution.ast.Environment;

public class MethodInvocation<R> extends AbstractMethodInvocation<R> {

	private Expression<ClassInstance> thisClass;
	
	public MethodInvocation(int classIndex, Expression<ClassInstance> thisClass, int methodIndex,
			Collection<Expression<?>> parameters, ClassDeclaration parentClass) {
		super(classIndex, methodIndex, parameters, parentClass);
		this.thisClass = thisClass;
	}

	@Override
	protected R callMethod(Object... parameters) throws InterruptedException {
		if (thisClass == null)
			return Environment.getInstance().callMethod(null, methodIndex, parameters);
		else
			return Environment.getInstance().callMethod(thisClass.evaluate(), methodIndex, parameters);
	}

	@Override
	public void assign(R value) throws IllegalStateException {
		throw new IllegalStateException("This expression is not an L-value");
	}

}
