package com.gamevm.execution.ast.tree;

import java.io.Serializable;

import com.gamevm.execution.ast.Environment;

public abstract class Statement extends TreeCodeInstruction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws InterruptedException {
		if (Environment.getInstance().isBreakPoint(this)) {
			Environment.getInstance().debug(this);

			synchronized (this) {
				wait();
			}

		}
	}

}
