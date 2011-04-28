package com.gamevm.execution.ast.tree;

import java.util.Collection;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.execution.ast.Environment;
import com.gamevm.utils.StringFormatter;

public abstract class AbstractMethodInvocation<R> extends NotAddressable<R> {
	
	protected int classIndex;
	protected int methodIndex;
	protected Collection<Expression<?>> parameters;
	
	protected String parentClassName;
	protected String methodName;
	
	public AbstractMethodInvocation(int classIndex, int methodIndex, Collection<Expression<?>> parameters, ClassDeclaration parentClass) {
		this.methodIndex = methodIndex;
		this.parameters = parameters;
		this.classIndex = classIndex;
		this.parentClassName = parentClass.getName();
		this.methodName = parentClass.getMethod(methodIndex).getName();
	}

	@Override
	public String toString(int ident) {
		return String.format("%s.%s(%s)", parentClassName, methodName, StringFormatter.printIterable(parameters, ", "));
	}
	
	protected abstract R callMethod(Object... parameters) throws InterruptedException;
	
	@Override
	public R evaluate() throws InterruptedException {
		super.evaluate();
		Object[] p = new Object[parameters.size()];
		int i = 0;
		for (Expression<?> e : parameters) {
			p[i++] = e.evaluate();
		}
		
		return callMethod(p);
	}

}
