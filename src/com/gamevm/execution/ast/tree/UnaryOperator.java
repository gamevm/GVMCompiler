package com.gamevm.execution.ast.tree;

import com.gamevm.utils.StringFormatter;

public abstract class UnaryOperator<R, P> extends NotAddressable<R> {

	private Expression<P> e;
	private String opString;
	
	public UnaryOperator(Expression<P> e, String opString) {
		this.e = e;
		this.opString = opString;
	}
	
	@Override
	public String toString(int ident) {
		return String.format("%s(%s%s)", StringFormatter.generateWhitespaces(ident), opString, e.toString(0));
	}
	
	protected abstract R op(P arg);

	@Override
	public R evaluate() {
		return op(e.evaluate());
	}

}
