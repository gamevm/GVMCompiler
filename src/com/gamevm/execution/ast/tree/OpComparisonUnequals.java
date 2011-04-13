package com.gamevm.execution.ast.tree;

public class OpComparisonUnequals extends BinaryOperator<Boolean, Object> {

	public OpComparisonUnequals(Expression<Object> a, Expression<Object> b) {
		super(a, b, "!=");
	}

	@Override
	protected Boolean op(Object a, Object b) {
		return a != b;
	}
	

}
