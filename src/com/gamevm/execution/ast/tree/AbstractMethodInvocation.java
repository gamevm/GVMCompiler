package com.gamevm.execution.ast.tree;

import java.util.Collection;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.execution.ast.Environment;
import com.gamevm.utils.StringFormatter;

public abstract class AbstractMethodInvocation<R, C> extends NotAddressable<R> {
	
	protected int classIndex;
	protected int methodIndex;
	protected Collection<Expression<?>> parameters;
	
	public AbstractMethodInvocation(int classIndex, int methodIndex, Collection<Expression<?>> parameters) {
		this.methodIndex = methodIndex;
		this.parameters = parameters;
		this.classIndex = classIndex;
	}

	@Override
	public String toString(int ident) {
		Method m = Environment.getClassInformation(classIndex).getMethod(methodIndex);
		return String.format("%s%s(%s)", StringFormatter.generateWhitespaces(ident), m.getName(), StringFormatter.printIterable(parameters, ", "));
	}
	
	protected abstract R callMethod(Object... parameters);
	
	@Override
	public R evaluate() {
		
		Object[] p = new Object[parameters.size()];
		int i = 0;
		for (Expression<?> e : parameters) {
			p[i++] = e.evaluate();
		}
		
		return callMethod(p);
	}

}
