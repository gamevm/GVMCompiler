package com.gamevm.execution.ast.tree;

import java.util.Collection;

import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.execution.ast.ClassInstance;
import com.gamevm.execution.ast.Environment;

public class MethodInvocation<R> extends AbstractMethodInvocation<R, ClassInstance> {

	private Expression<ClassInstance> thisClass;
	
	public MethodInvocation(int classIndex, Expression<ClassInstance> thisClass, int methodIndex,
			Collection<Expression<?>> parameters) {
		super(classIndex, methodIndex, parameters);
		this.thisClass = thisClass;
	}

	@Override
	protected R callMethod(Object... parameters) {
		if (thisClass == null)
			return Environment.callMethod(null, methodIndex, parameters);
		else
			return Environment.callMethod(thisClass.evaluate(), methodIndex, parameters);
	}

	@Override
	public void assign(R value) throws IllegalStateException {
		throw new IllegalStateException("This expression is not an L-value");
	}

}
