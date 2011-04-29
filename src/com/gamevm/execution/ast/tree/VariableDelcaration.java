package com.gamevm.execution.ast.tree;

import java.io.IOException;

import com.gamevm.compiler.Type;
import com.gamevm.execution.ast.Environment;
import com.gamevm.utils.StringFormatter;

public class VariableDelcaration<T> extends Statement {
	
	private Expression<T> initialization;
	private T value;
	private Type type;
	private String name;
	private int index;
	
	public VariableDelcaration(int index, Type type, String name, Expression<T> initialization) {
		value = (T)type.getDefaultValue();
		this.type = type;
		this.initialization = initialization;
		this.index = index;
		this.name = name;
	}
	
	@Override
	public String toString(int ident) {
		String ws = StringFormatter.generateWhitespaces(ident);
		if (initialization != null)
			return String.format("%s%s %s{$%d} = %s;", ws, type, name, index, initialization.toString(0));
		else
			return String.format("%s%s %s{$%d};", ws, type, name, index);
				
	}

	@Override
	public void execute() throws InterruptedException {
		super.execute();
		if (initialization != null) {
			value = initialization.evaluate();
		}
		Environment.getInstance().addVariable(value);
	}
	
	@Override
	public String toString() {
		return toString(0);
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(initialization);
		out.writeObject(value);
		out.writeUTF(name);
		out.writeInt(index);
		out.writeUTF(type.getName());
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		initialization = (Expression<T>)in.readObject();
		value = (T)in.readObject();
		name = in.readUTF();
		index = in.readInt();
		type = Type.getType(in.readUTF());
	}

}
