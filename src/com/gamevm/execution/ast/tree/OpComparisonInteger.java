package com.gamevm.execution.ast.tree;

import com.gamevm.compiler.parser.ASTNode;

public class OpComparisonInteger extends BinaryOperator<Boolean, Integer> {

	private int op;
	
	public OpComparisonInteger(Expression<Integer> a, Expression<Integer> b, int op) {
		super(a, b, Operator.getOperatorString(op));
		this.op = op;
	}

	@Override
	protected Boolean op(Integer a, Integer b) {
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
