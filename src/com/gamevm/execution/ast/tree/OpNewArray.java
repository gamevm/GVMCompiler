package com.gamevm.execution.ast.tree;

import java.io.IOException;
import java.util.Collection;

import com.gamevm.compiler.assembly.Type;
import com.gamevm.execution.ast.builtin.ArrayInstance;
import com.gamevm.utils.StringFormatter;

public class OpNewArray extends NotAddressable<ArrayInstance> {
	
	private Object defaultValue;
	private Collection<Expression<Integer>> dimensions;
	
	private Type elementType; // for debugging
	
	public OpNewArray(Object defaultValue,
			Collection<Expression<Integer>> dimensions, Type elementType) {
		this.defaultValue = defaultValue;
		this.dimensions = dimensions;
		this.elementType = elementType;
	}

	@Override
	public String toString(int ident) {
		return String.format("%snew %s[%s]", StringFormatter.generateWhitespaces(ident), elementType.getName(), StringFormatter.printIterable(dimensions, "]["));
	}

	protected ArrayInstance evaluate(int depth, int[] sizes) {
		if (depth == sizes.length - 1) {
			return new ArrayInstance(defaultValue, sizes[depth]);
		} else {
			return new ArrayInstance(evaluate(depth+1, sizes), sizes[depth]);
		}
	}
	
	@Override
	public ArrayInstance evaluate() throws InterruptedException {
		super.evaluate();
		int[] dim = new int[dimensions.size()];
		int i = 0;
		for (Expression<Integer> d : dimensions) {
			dim[i++] = d.evaluate();
		}
		return evaluate(0, dim);
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(defaultValue);
		out.writeObject(dimensions);
		out.writeUTF(elementType.getName());
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		defaultValue = in.readObject();
		dimensions = (Collection<Expression<Integer>>)in.readObject();
		String typeName = in.readUTF();
		elementType = Type.getType(typeName);
	}

}
