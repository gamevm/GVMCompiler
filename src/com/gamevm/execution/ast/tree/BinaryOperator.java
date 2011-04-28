package com.gamevm.execution.ast.tree;

import com.gamevm.compiler.assembly.InstructionVisitor;
import com.gamevm.utils.StringFormatter;

public abstract class BinaryOperator<R, P> extends NotAddressable<R> {

	private Expression<P> a;
	private Expression<P> b;
	private String opString;
	
	public BinaryOperator(Expression<P> a, Expression<P> b, String opString) {
		this.a = a;
		this.b = b;
		this.opString = opString;
	}
	
	protected abstract R op(P a, P b);
	
	@Override
	public String toString(int ident) {
		return String.format("(%s %s %s)", a.toString(0), opString, b.toString(0));
	}

	@Override
	public R evaluate() throws InterruptedException {
		super.evaluate();
		return op(a.evaluate(), b.evaluate());
	}

}
