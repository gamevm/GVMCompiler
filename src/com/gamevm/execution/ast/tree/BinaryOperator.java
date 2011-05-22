package com.gamevm.execution.ast.tree;


public abstract class BinaryOperator<T> extends NotAddressable {

	private static final long serialVersionUID = 1L;
	private Expression a;
	private Expression b;
	private String opString;
	
	public BinaryOperator(Expression a, Expression b, String opString) {
		this.a = a;
		this.b = b;
		this.opString = opString;
	}
	
	protected abstract Object op(T a, T b);
	
	@Override
	public String toString(int ident) {
		return String.format("(%s %s %s)", (a != null) ? a.toString(0) : null, opString, (b != null) ? b.toString(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object evaluate() throws InterruptedException {
		super.evaluate();
		return op((T)a.evaluate(), (T)b.evaluate());
	}

}
