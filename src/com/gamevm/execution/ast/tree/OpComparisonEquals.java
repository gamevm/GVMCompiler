package com.gamevm.execution.ast.tree;

public class OpComparisonEquals extends BinaryOperator<Object> {

	private static final long serialVersionUID = 1L;

	public OpComparisonEquals(Expression a, Expression b) {
		super(a, b, "==");
	}

	@Override
	protected Boolean op(Object a, Object b) {
		if (a instanceof Number || a instanceof Boolean || a instanceof Character)
			return a.equals(b);
		else
			return a == b;
	}
	

}
