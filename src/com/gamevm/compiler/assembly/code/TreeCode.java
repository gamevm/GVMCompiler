package com.gamevm.compiler.assembly.code;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public abstract class TreeCode<T> implements Code {
	
	private T root;
	
	public TreeCode() {
		root = null;
	}
	
	public TreeCode(T root) {
		this.root = root;
	}

	@Override
	public void write(ObjectOutputStream out) throws IOException {
		out.writeObject(root);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void read(ObjectInputStream in) throws IOException {
		try {
			root = (T)in.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
	}
	
	public T getRoot() {
		return root;
	}

}
