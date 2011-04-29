package com.gamevm.execution.ast.tree;

import java.io.IOException;

import com.gamevm.compiler.Type;
import com.gamevm.execution.ast.builtin.StringInstance;
import com.gamevm.utils.StringFormatter;

public class Literal<T> extends NotAddressable<T> {

	private T value;

	public Literal(T value) {
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
	public T evaluate() throws InterruptedException {
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

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		Object o = in.readObject();
		if (o instanceof String) {
			value = (T)new StringInstance((String)o);
		} else {
			value = (T)o;
		}
	}

}
