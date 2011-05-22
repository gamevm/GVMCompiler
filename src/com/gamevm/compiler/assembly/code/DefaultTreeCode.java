package com.gamevm.compiler.assembly.code;

import com.gamevm.Indentable;

public class DefaultTreeCode<T extends Indentable> extends TreeCode<T> {
	
	public DefaultTreeCode() {
		super();
	}
	
	public DefaultTreeCode(T root) {
		super(root);
	}

	@Override
	public String toString(int indent) {
		return getRoot().toString(indent);
	}

}
