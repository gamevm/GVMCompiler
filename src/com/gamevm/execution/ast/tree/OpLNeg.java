package com.gamevm.execution.ast.tree;

public class OpLNeg extends UnaryOperator<Boolean, Boolean> {

	public OpLNeg(Expression<Boolean> e) {
		super(e, "!");
	}

	@Override
	protected Boolean op(Boolean arg) {
		return !arg;
	}

}
