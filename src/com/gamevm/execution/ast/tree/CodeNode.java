package com.gamevm.execution.ast.tree;

import com.gamevm.Indentable;

public abstract class CodeNode implements Indentable {
	
	@Override
	public String toString() {
		return toString(0);
	}
	
	public abstract int getMaxLocals();

}
