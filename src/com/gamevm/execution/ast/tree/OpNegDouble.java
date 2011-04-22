package com.gamevm.execution.ast.tree;

public class OpNegDouble extends UnaryOperator<Double, Double> {

	public OpNegDouble(Expression<Double> e) {
		super(e, "-");
	}

	@Override
	protected Double op(Double arg) {
		return -arg;
	}

}
