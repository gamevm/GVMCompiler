package com.gamevm.execution.ast.tree;

import java.io.Serializable;

import com.gamevm.execution.ast.Environment;

public abstract class Expression extends TreeCodeInstruction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Object evaluate() throws InterruptedException {
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
	public abstract void assign(Object value) throws IllegalStateException, InterruptedException;

}
