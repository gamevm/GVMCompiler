package com.gamevm.execution.ast.tree;

public class OpNegLong extends UnaryOperator<Long, Long> {

	public OpNegLong(Expression<Long> e) {
		super(e, "-");
	}

	@Override
	protected Long op(Long arg) {
		return -arg;
	}

}
