package com.gamevm.execution.ast.tree;

public class OpNegFloat extends UnaryOperator<Float, Float> {

	public OpNegFloat(Expression<Float> e) {
		super(e, "-");
	}

	@Override
	protected Float op(Float arg) {
		return -arg;
	}

}
