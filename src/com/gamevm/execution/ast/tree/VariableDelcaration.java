package com.gamevm.execution.ast.tree;

import com.gamevm.compiler.assembly.Type;
import com.gamevm.execution.ast.Environment;
import com.gamevm.utils.StringFormatter;

public class VariableDelcaration<T> implements Statement {
	
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
			return String.format("%s%s %s = %s;", ws, type, name, initialization.toString(0));
		else
			return String.format("%s%s %s;", ws, type, name);
				
	}

	@Override
	public void execute() {
		if (initialization != null) {
			value = initialization.evaluate();
		}
		Environment.addVariable(value);
	}

}
