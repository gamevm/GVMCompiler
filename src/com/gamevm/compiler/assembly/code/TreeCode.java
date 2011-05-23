package com.gamevm.compiler.assembly.code;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public abstract class TreeCode<T> implements Code {
	
	private static class EmptyTree implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 5159983074767705690L;

		@Override
		public boolean equals(Object obj) {
			return obj instanceof EmptyTree;
		}
		
	}
	
	private static EmptyTree EMPTY = new EmptyTree();
	
	private T root;
	
	public TreeCode() {
		root = null;
	}
	
	public TreeCode(T root) {
		this.root = root;
	}

	@Override
	public void write(ObjectOutputStream out) throws IOException {
		if (root != null)
			out.writeObject(root);
		else
			out.writeObject(EMPTY);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void read(ObjectInputStream in) throws IOException {
		try {
			Object o = in.readObject();
			if (o.equals(EMPTY))
				root = null;
			else
				root = (T)o;
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
	}
	
	public T getRoot() {
		return root;
	}

}
