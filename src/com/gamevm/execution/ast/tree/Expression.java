package com.gamevm.execution.ast.tree;

import com.gamevm.compiler.assembly.AbstractInstruction;
import com.gamevm.compiler.assembly.Instruction;
import com.gamevm.execution.ast.Environment;

public abstract class Expression<T> extends AbstractInstruction {

	public T evaluate() throws InterruptedException {
		if (Environment.getInstance().isBreakPoint(this)) {
			Environment.getInstance().debug(this);

			synchronized (this) {
				wait();
			}

		}
		return null;
	}

	/**
	 * Assigns the given value to the lvalue represented by this expression.
	 * Note that not every expression is allowed as an L-value and therefore
	 * this method may throw an exception
	 * 
	 * @param value
	 */
	public abstract void assign(T value) throws IllegalStateException, InterruptedException;

}
