package com.gamevm.compiler.parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Variable {

	private static short counter = 0;
	
	public static Map<String, Variable> variables = new HashMap<String, Variable>();
	
	public static void declareVariable(Class<?> type, String name) {
		variables.put(name, new Variable(name, type));
	}
	
	public static void declareVariable(Class<?> type, String name, Object initialValue) {
		variables.put(name, new Variable(name, type, initialValue));
	}
	
	private Object initialValue;
	private String name;
	private short internal;
	private Class<?> type;
	
	public Variable(String name, Class<?> type) {
		this.name = name;
		this.type = type;
		this.internal = counter++;
	}
	
	public Variable(String name, Class<?> type, Object initialValue) {
		this.name = name;
		this.type = type;
		this.initialValue = initialValue;
		this.internal = counter++;
	}
	
//	@Override
//	public Object evaluate(Map<String, Object> environment) {
//		Object result = environment.get(name);
//		if (result == null)
//			System.err.println("ERROR: unknown variable " + name);
//		return result;
//	}
	
	public Instruction load() {
		if (Number.class.isAssignableFrom(type)) {
			return new Instruction(Instruction.OP_ILOAD, internal);
		} else if (type.equals(String.class)) {
			return new Instruction(Instruction.OP_SLOAD, internal); 
		}
		throw new IllegalStateException();
	}
	
	public Instruction store() {
		if (Number.class.isAssignableFrom(type)) {
			return new Instruction(Instruction.OP_ISTORE, internal);
		} else if (type.equals(String.class)) {
			return new Instruction(Instruction.OP_SSTORE, internal); 
		}
		throw new IllegalStateException();
	}
	
	public String toString() {
		return name;
	}

//	@Override
//	public Class<?> inferType() {
//		return type;
//	}
	
	public Class<?> getType() {
		return type;
	}
	
	public void initialize() {
		Instruction.variableMemory[internal] = initialValue;
	}

//	@Override
//	public Collection<Instruction> compile() throws CompilationException {
//		Collection<Instruction> instr = new LinkedList<Instruction>();
//		instr.add(load());
//		return instr;
//	}

}
