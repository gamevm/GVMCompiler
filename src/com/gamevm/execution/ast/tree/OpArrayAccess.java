package com.gamevm.execution.ast.tree;

import com.gamevm.execution.ast.builtin.ArrayInstance;

public class OpArrayAccess extends Expression {

	private static final long serialVersionUID = 1L;
	private Expression left;
	private Expression indexExpression;

	public OpArrayAccess(Expression left,
			Expression indexExpression) {
		this.left = left;
		this.indexExpression = indexExpression;
	}

	@Override
	public String toString(int ident) {
		return String.format("%s[%s]", left.toString(0), indexExpression.toString(0));
	}
	
	@Override
	public Object evaluate() throws InterruptedException {
		super.evaluate();
		return ((ArrayInstance)left.evaluate()).get((Integer)indexExpression.evaluate());
	}

	@Override
	public void assign(Object value) throws IllegalStateException, InterruptedException {
		((ArrayInstance)left.evaluate()).set((Integer)indexExpression.evaluate(), value);
	}
	
	@Override
	public String toString() {
		return toString(0);
	}

}
