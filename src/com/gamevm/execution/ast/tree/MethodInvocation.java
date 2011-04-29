package com.gamevm.execution.ast.tree;

import java.util.Collection;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.execution.ast.ClassInstance;
import com.gamevm.execution.ast.Environment;

public class MethodInvocation extends AbstractMethodInvocation {
	
	private static final long serialVersionUID = 1L;
	private Expression thisClass;
	
	public MethodInvocation(int classIndex, Expression thisClass, int methodIndex,
			Collection<Expression> parameters, ClassDeclaration parentClass) {
		super(classIndex, methodIndex, parameters, parentClass);
		this.thisClass = thisClass;
	}

	@Override
	protected Object callMethod(Object... parameters) throws InterruptedException {
		if (thisClass == null)
			return Environment.getInstance().callMethod(null, methodIndex, parameters);
		else
			return Environment.getInstance().callMethod((ClassInstance)thisClass.evaluate(), methodIndex, parameters);
	}

}
