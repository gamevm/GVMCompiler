package com.gamevm.execution.ast.tree;

import com.gamevm.compiler.assembly.InstructionVisitor;
import com.gamevm.compiler.assembly.Type;
import com.gamevm.utils.StringFormatter;

public class Cast<T> extends NotAddressable<T> {
	
	private Expression<?> e;
	private Type targetType;
	
	public Cast(Expression<?> e, Type targetType) {
		this.e = e;
		this.targetType = targetType;
	}

	@Override
	public String toString(int ident) {
		return String.format("%s(%s)%s", StringFormatter.generateWhitespaces(ident), targetType, e.toString(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public T evaluate() throws InterruptedException {
		super.evaluate();
		return (T)e.evaluate();
	}

}
