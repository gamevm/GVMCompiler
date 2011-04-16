package com.gamevm.execution.ast.tree;

import com.gamevm.compiler.assembly.AbstractInstruction;
import com.gamevm.execution.ast.Environment;

public abstract class Statement extends AbstractInstruction {

	public void execute() throws InterruptedException {
		if (Environment.getInstance().isBreakPoint(this)) {
			Environment.getInstance().debug(this);

			synchronized (this) {
				wait();
			}

		}
	}

}
