package com.gamevm.execution.ast.tree;

public class OpNegInteger extends UnaryOperator<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OpNegInteger(Expression e) {
		super(e, "-");
	}

	@Override
	protected Integer op(Integer arg) {
		return -arg;
	}

}
