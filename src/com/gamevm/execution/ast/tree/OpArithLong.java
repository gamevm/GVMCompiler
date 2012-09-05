package com.gamevm.execution.ast.tree;

import com.gamevm.compiler.parser.ASTNode;

public class OpArithLong extends BinaryOperator<Long> {

	private static final long serialVersionUID = 1L;
	private int op;
	
	public OpArithLong(Expression a, Expression b, int op) {
		super(a, b, Operator.getOperatorString(op));
		this.op = op;
	}

	@Override
	protected Long op(Long a, Long b) {
		switch (op) {
		case ASTNode.TYPE_OP_PLUS:
			return a + b;
		case ASTNode.TYPE_OP_MINUS:
			return a - b;
		case ASTNode.TYPE_OP_MULT:
			return a * b;
		case ASTNode.TYPE_OP_DIV:
			return a / b;
		case ASTNode.TYPE_OP_MOD:
			return a % b;
		default:
			throw new IllegalStateException("Operator " + Operator.getOperatorString(op) + " not allowed in this context.");
		}
	}

}
