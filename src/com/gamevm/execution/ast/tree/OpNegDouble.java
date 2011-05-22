package com.gamevm.execution.ast.tree;

public class OpNegDouble extends UnaryOperator<Double> {

	private static final long serialVersionUID = 1L;

	public OpNegDouble(Expression e) {
		super(e, "-");
	}

	@Override
	protected Double op(Double arg) {
		return -arg;
	}

}
