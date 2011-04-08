package com.gamevm.compiler.parser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;

import com.gamevm.compiler.parser.Variable;

public class Instruction {

	public static String[] opCodeStr = new String[] { "OP_ILOAD", "OP_ISTORE",
			"OP_IADD", "OP_ICONST", "OP_SLOAD", "OP_SADD", "OP_SCONST",
			"OP_SSTORE", "OP_SCONV", "OP_IMULT", "OP_IDIV", "OP_IMOD",
			"OP_ISUB", "OP_IEQN", "OP_ILEQ", "OP_IGEQ", "OP_ILTH", "OP_IGTH",
			"OP_IEQU", "OP_INEQ", "OP_ILAN", "OP_ILOR", "OP_IJMP", "OP_GOTO", "OP_PRINT" };

	public static final short OP_ILOAD = 0;
	public static final short OP_ISTORE = 1;
	public static final short OP_IADD = 2;
	public static final short OP_ICONST = 3;
	public static final short OP_SLOAD = 4;
	public static final short OP_SADD = 5;
	public static final short OP_SCONST = 6;
	public static final short OP_SSTORE = 7;
	public static final short OP_SCONV = 8;
	public static final short OP_IMULT = 9;
	public static final short OP_IDIV = 10;
	public static final short OP_IMOD = 11;
	public static final short OP_ISUB = 12;
	public static final short OP_IEQN = 13;
	public static final short OP_ILEQ = 14;
	public static final short OP_IGEQ = 15;
	public static final short OP_ILTH = 16;
	public static final short OP_IGTH = 17;
	public static final short OP_IEQU = 18;
	public static final short OP_INEQ = 19;
	public static final short OP_ILAN = 20;
	public static final short OP_ILOR = 21;
	public static final short OP_JMP = 22;
	public static final short OP_GOTO = 23;

	public static final short OP_PRINT = 24;

	static Object[] variableMemory = new Object[16];
	static Stack<Long> intStack = new Stack<Long>();
	static Stack<String> stringStack = new Stack<String>();

	static Stack<String> stringLiterals = new Stack<String>();

	private short opCode;
	private short argument;

	public static Collection<Instruction> newInstruction(short opCode,
			short argument) {
		LinkedList<Instruction> result = new LinkedList<Instruction>();
		result.add(new Instruction(opCode, argument));
		return result;
	}

	public Instruction(short opCode, short argument) {
		this.opCode = opCode;
		this.argument = argument;
	}

	public static Instruction loadInteger(short variable) {
		return new Instruction(OP_ILOAD, variable);
	}

	public static Instruction storeInteger(short variable) {
		return new Instruction(OP_ISTORE, variable);
	}

	public void write(DataOutputStream output) throws IOException {
		output.writeShort(opCode);
		output.writeShort(argument);
	}

	private static int pc;

	public static void execute(Instruction[] instructions) {
		pc = 0;
		
		// init:
		for (Variable v : Variable.variables.values()) {
			v.initialize();
		}

		while (pc < instructions.length) {
			instructions[pc].execute();
			++pc;
		}
	}

	private static void incPC(int val) {
		pc += val;
	}
	
	private static void setPC(int val) {
		pc = val;
	}

	private void execute() {
		System.out.println("Executing " + this);
		switch (opCode) {
		case OP_ILOAD:
			intStack.push((Long) variableMemory[argument]);
			break;
		case OP_ISTORE:
			variableMemory[argument] = intStack.pop();
			break;
		case OP_IADD:
			intStack.push(intStack.pop() + intStack.pop());
			break;
		case OP_ICONST:
			intStack.push((long) argument);
			break;
		case OP_SLOAD:
			stringStack.push(variableMemory[argument].toString());
			break;
		case OP_SADD:
			stringStack.push(stringStack.pop() + stringStack.pop());
			break;
		case OP_SCONST:
			stringStack.push(stringLiterals.get(argument));
			break;
		case OP_SSTORE:
			variableMemory[argument] = stringStack.pop();
			break;
		case OP_SCONV:
			stringStack.push(intStack.pop().toString());
			break;
		case OP_PRINT:
			System.out.println(stringStack.pop());
			break;
		case OP_IMULT:
			intStack.push(intStack.pop() * intStack.pop());
			break;
		case OP_IDIV:
			intStack.push(intStack.pop() / intStack.pop());
			break;
		case OP_IMOD:
			intStack.push(intStack.pop() % intStack.pop());
			break;
		case OP_ISUB:
			intStack.push(intStack.pop() - intStack.pop());
			break;
		case OP_ILEQ:
			intStack.push((intStack.pop() <= intStack.pop()) ? 1L : 0L);
			break;
		case OP_IGEQ:
			intStack.push((intStack.pop() >= intStack.pop()) ? 1L : 0L);
			break;
		case OP_ILTH:
			intStack.push((intStack.pop() < intStack.pop()) ? 1L : 0L);
			break;
		case OP_IGTH:
			intStack.push((intStack.pop() > intStack.pop()) ? 1L : 0L);
			break;
		case OP_IEQU:
			intStack.push((intStack.pop() == intStack.pop()) ? 1L : 0L);
			break;
		case OP_INEQ:
			intStack.push((intStack.pop() != intStack.pop()) ? 1L : 0L);
			break;
		case OP_ILAN:
			intStack.push(((intStack.pop() == 1L) && (intStack.pop() == 1L)) ? 1L
					: 0L);
			break;
		case OP_ILOR:
			intStack.push(((intStack.pop() == 1L) || (intStack.pop() == 1L)) ? 1L
					: 0L);
			break;
		case OP_IEQN:
			if (intStack.pop() == 0L) {
				incPC(argument);
			}
			break;
		case OP_JMP:
			incPC(argument);
			break;
		case OP_GOTO:
			setPC(argument);
			break;
		}
		System.out.format("%s %s %s\n", intStack, stringStack,
				Arrays.toString(variableMemory));
	}

	@Override
	public String toString() {
		return String.format("(%s,%d)", opCodeStr[opCode], argument);
	}

}
