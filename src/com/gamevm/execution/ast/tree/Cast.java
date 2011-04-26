package com.gamevm.execution.ast.tree;

import java.io.IOException;

import com.gamevm.compiler.assembly.Type;
import com.gamevm.execution.ast.ClassInstance;
import com.gamevm.execution.ast.builtin.StringInstance;
import com.gamevm.utils.StringFormatter;

public class Cast<T> extends NotAddressable<T> {

	private Expression<?> e;
	private Type targetType;

	public Cast(Expression<?> e, Type targetType) {
		this.e = e;
		this.targetType = targetType;
	}

	public static Cast<?> getCast(Expression<?> e, Type targetType) {
		switch (targetType.ordinal()) {
		case Type.ORDINAL_BOOLEAN:
			return new Cast<Boolean>(e, targetType);
		case Type.ORDINAL_BYTE:
			return new Cast<Boolean>(e, targetType);
		case Type.ORDINAL_CHAR:
			return new Cast<Boolean>(e, targetType);
		case Type.ORDINAL_DOUBLE:
			return new Cast<Boolean>(e, targetType);
		case Type.ORDINAL_FLOAT:
			return new Cast<Boolean>(e, targetType);
		case Type.ORDINAL_INT:
			return new Cast<Boolean>(e, targetType);
		case Type.ORDINAL_LONG:
			return new Cast<Boolean>(e, targetType);
		case Type.ORDINAL_SHORT:
			return new Cast<Boolean>(e, targetType);
		default:
			return new Cast<ClassInstance>(e, targetType);
		}
	}

	@Override
	public String toString(int ident) {
		return String.format("%s(%s)%s", StringFormatter.generateWhitespaces(ident), targetType, e.toString(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public T evaluate() throws InterruptedException {
		super.evaluate();
		Object o = e.evaluate();
		if (targetType == Type.STRING) {
			return (T) new StringInstance(o.toString());
		} else if (!targetType.isPrimitive()) {
			// TODO
			return (T) o;
		} else {
			return (T) o;
		}
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(e);
		out.writeUTF(targetType.getName());
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		e = (Expression<?>)in.readObject();
		String typeName = in.readUTF();
		targetType = Type.getType(typeName);
	}

}
