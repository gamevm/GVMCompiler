package com.gamevm.execution.ast.tree;

public class OpNegFloat extends UnaryOperator<Float> {

	private static final long serialVersionUID = 1L;

	public OpNegFloat(Expression e) {
		super(e, "-");
	}

	@Override
	protected Float op(Float arg) {
		return -arg;
	}

}
