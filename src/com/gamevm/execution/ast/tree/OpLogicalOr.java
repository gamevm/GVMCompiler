package com.gamevm.execution.ast.tree;

public class OpLogicalOr extends BinaryOperator<Boolean, Boolean> {

	public OpLogicalOr(Expression<Boolean> a, Expression<Boolean> b) {
		super(a, b, "||");
	}

	@Override
	protected Boolean op(Boolean a, Boolean b) {
		return a || b;
	}

}
