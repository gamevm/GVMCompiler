package com.gamevm.execution.ast.tree;

public class OpComparisonEquals extends BinaryOperator<Boolean, Object> {

	public OpComparisonEquals(Expression<Object> a, Expression<Object> b) {
		super(a, b, "==");
	}

	@Override
	protected Boolean op(Object a, Object b) {
		return a == b;
	}
	

}
