package com.gamevm.execution.ast.tree;

public class OpComparisonUnequals extends BinaryOperator<Object> {

	private static final long serialVersionUID = 1L;

	public OpComparisonUnequals(Expression a, Expression b) {
		super(a, b, "!=");
	}

	@Override
	protected Boolean op(Object a, Object b) {
		if (a instanceof Number || a instanceof Boolean || a instanceof Character)
			return !a.equals(b);
		else
			return a != b;
	}
	

}
