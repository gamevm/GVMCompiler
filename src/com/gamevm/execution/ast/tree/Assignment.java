package com.gamevm.execution.ast.tree;


public class Assignment extends NotAddressable {

	private static final long serialVersionUID = 1L;
	private Expression lvalue;
	private Expression rvalue;
	
	public Assignment(Expression lvalue, Expression rvalue) {
		this.lvalue = lvalue;
		this.rvalue = rvalue;
	}
	
	@Override
	public String toString(int ident) {
		return lvalue.toString(0) + " = " + rvalue.toString(0);
	}

	@Override
	public Object evaluate() throws InterruptedException {
		super.evaluate();
		final Object v = rvalue.evaluate();
		lvalue.assign(v);
		return v;
	}

}
