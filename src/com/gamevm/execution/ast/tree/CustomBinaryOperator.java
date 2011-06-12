package com.gamevm.execution.ast.tree;

import com.gamevm.execution.ast.ClassInstance;
import com.gamevm.execution.ast.Environment;


public class CustomBinaryOperator extends BinaryOperator<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9121420702046938408L;
	
	private boolean leftHanded;
	private int method;

	public CustomBinaryOperator(Expression a, Expression b, int method, boolean leftHanded, String opString) {
		super(a, b, opString);
		this.leftHanded = leftHanded;
		this.method = method;
	}

	@Override
	protected Object op(Object a, Object b) throws InterruptedException {
		ClassInstance instance = (ClassInstance)((leftHanded) ? a : b);
		Object argument = (leftHanded) ? b : a;
		return Environment.getInstance().callMethod(instance, method, argument);
	}
	
	

}
