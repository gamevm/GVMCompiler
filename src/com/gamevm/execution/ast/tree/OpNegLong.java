package com.gamevm.execution.ast.tree;

public class OpNegLong extends UnaryOperator<Long> {

	private static final long serialVersionUID = 1L;

	public OpNegLong(Expression e) {
		super(e, "-");
	}

	@Override
	protected Long op(Long arg) {
		return -arg;
	}

}
