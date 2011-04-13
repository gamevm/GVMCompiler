package com.gamevm.execution.ast.tree;

import javax.naming.OperationNotSupportedException;

import com.gamevm.compiler.assembly.Instruction;

public interface Expression<T> extends Instruction {

	public T evaluate();

	/**
	 * Assigns the given value to the lvalue represented by this expression.
	 * Note that not every expression is allowed as an L-value and therefore
	 * this method may throw an exception
	 * 
	 * @param value
	 */
	public void assign(T value) throws IllegalStateException;

}
