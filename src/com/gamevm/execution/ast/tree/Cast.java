package com.gamevm.execution.ast.tree;

import java.io.IOException;

import com.gamevm.compiler.Type;
import com.gamevm.execution.ast.builtin.StringInstance;

public class Cast extends NotAddressable {

	private static final long serialVersionUID = 1L;
	private Expression e;
	private Type targetType;

	public Cast(Expression e, Type targetType) {
//		if (e == null)
//			throw new IllegalArgumentException("Expression may not be null");
		this.e = e;
		this.targetType = targetType;
	}

	@Override
	public String toString(int ident) {
		return String.format("(%s)%s", targetType, (e != null) ? e.toString(0) : "null");
	}

	@Override
	public Object evaluate() throws InterruptedException {
		super.evaluate();
		Object o = e.evaluate();
		if (targetType == Type.STRING) {
			return new StringInstance(o.toString());
		} else if (!targetType.isPrimitive()) {
			// TODO
			return o;
		} else {
			final Number n;
			if (o instanceof Number)
				n = (Number) o;
			else
				n = Integer.valueOf(((Character) o).charValue());
			if (targetType == Type.BYTE)
				return n.byteValue();
			else if (targetType == Type.SHORT)
				return n.shortValue();
			else if (targetType == Type.INT)
				return n.intValue();
			else if (targetType == Type.LONG)
				return n.longValue();
			else if (targetType == Type.FLOAT)
				return n.floatValue();
			else if (targetType == Type.DOUBLE)
				return n.doubleValue();
			
			return null;
		}
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(e);
		out.writeUTF(targetType.getName());
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		e = (Expression) in.readObject();
		String typeName = in.readUTF();
		targetType = Type.getType(typeName);
	}

}
