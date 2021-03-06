package com.gamevm.execution.ast.tree;

public class OpLogicalAnd extends BinaryOperator<Boolean> {

	private static final long serialVersionUID = 1L;

	public OpLogicalAnd(Expression a, Expression b) {
		super(a, b, "&&");
	}

	@Override
	protected Boolean op(Boolean a, Boolean b) {
		return a && b;
	}


}
