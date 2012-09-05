package com.gamevm.execution.ast.tree;

import java.io.IOException;

import com.gamevm.execution.ast.builtin.StringInstance;

public class Literal extends NotAddressable {

	private static final long serialVersionUID = 1L;
	private Object value;

	public Literal(Object value) {
		this.value = value;
	}

	@Override
	public String toString(int ident) {
		if (value instanceof StringInstance) {
			return String.format("\"%s\"", value.toString());
		} else {
			return String.format("%s", value.toString());
		}
	}

	@Override
	public Object evaluate() throws InterruptedException {
		super.evaluate();
		return value;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		if (value instanceof StringInstance) {
			out.writeObject(((StringInstance) value).getInternal());
		} else {
			out.writeObject(value);
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		Object o = in.readObject();
		if (o instanceof String) {
			value = new StringInstance((String)o);
		} else {
			value = o;
		}
	}

}
