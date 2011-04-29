package com.gamevm.execution.ast.tree;


public abstract class UnaryOperator<P> extends NotAddressable {

	private static final long serialVersionUID = 1L;
	private Expression e;
	private String opString;
	
	public UnaryOperator(Expression e, String opString) {
		this.e = e;
		this.opString = opString;
	}
	
	@Override
	public String toString(int ident) {
		return String.format("(%s%s)", opString, e.toString(0));
	}
	
	protected abstract Object op(P arg);

	@SuppressWarnings("unchecked")
	@Override
	public Object evaluate() throws InterruptedException {
		super.evaluate();
		return op((P)e.evaluate());
	}

}
