package com.gamevm.execution.ast.tree;

public class OpLogicalAnd extends BinaryOperator<Boolean, Boolean> {

	public OpLogicalAnd(Expression<Boolean> a, Expression<Boolean> b) {
		super(a, b, "&&");
	}

	@Override
	protected Boolean op(Boolean a, Boolean b) {
		return a && b;
	}


}
