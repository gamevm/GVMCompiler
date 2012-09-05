package com.gamevm.execution.ast.tree;

public class OpLNeg extends UnaryOperator<Boolean> {

	private static final long serialVersionUID = 1L;

	public OpLNeg(Expression e) {
		super(e, "!");
	}

	@Override
	protected Object op(Boolean arg) {
		return !arg;
	}

}
