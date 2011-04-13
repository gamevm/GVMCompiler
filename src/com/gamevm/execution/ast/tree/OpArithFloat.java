package com.gamevm.execution.ast.tree;

import com.gamevm.compiler.parser.ASTNode;

public class OpArithFloat extends BinaryOperator<Float, Float> {

	private int op;
	
	public OpArithFloat(Expression<Float> a, Expression<Float> b, int op) {
		super(a, b, Operator.getOperatorString(op));
		this.op = op;
	}

	@Override
	protected Float op(Float a, Float b) {
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

	@Override
	public void assign(Float value) throws IllegalStateException {
		throw new IllegalStateException("This expression is not an L-value");
	}

}
