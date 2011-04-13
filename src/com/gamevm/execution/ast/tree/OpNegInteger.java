package com.gamevm.execution.ast.tree;

public class OpNegInteger extends UnaryOperator<Integer, Integer> {

	public OpNegInteger(Expression<Integer> e) {
		super(e, "-");
	}

	@Override
	protected Integer op(Integer arg) {
		return -arg;
	}

}
