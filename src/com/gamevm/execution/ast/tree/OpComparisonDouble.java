package com.gamevm.execution.ast.tree;

import com.gamevm.compiler.parser.ASTNode;

public class OpComparisonDouble extends BinaryOperator<Double> {

	private static final long serialVersionUID = 1L;
	private int op;
	
	public OpComparisonDouble(Expression a, Expression b, int op) {
		super(a, b, Operator.getOperatorString(op));
		this.op = op;
	}

	@Override
	protected Boolean op(Double a, Double b) {
		switch (op) {
		case ASTNode.TYPE_OP_GTH:
			return a > b;
		case ASTNode.TYPE_OP_LTH:
			return a < b;
		case ASTNode.TYPE_OP_GEQ:
			return a >= b;
		case ASTNode.TYPE_OP_LEQ:
			return a <= b;
		default:
			throw new IllegalStateException("Operator " + Operator.getOperatorString(op) + " not allowed in this context.");
		}
	}

}
