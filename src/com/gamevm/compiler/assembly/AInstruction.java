package com.gamevm.compiler.assembly;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class AInstruction {
	
	public static String[] opCodeStr = new String[] {};

	public static final short OP_ILOAD = 0;
	public static final short OP_ISTORE = 1;
	public static final short OP_ICONST = 3;
	public static final short OP_IADD = 3;
	public static final short OP_ISUB = 4;
	public static final short OP_IMULT = 5;
	public static final short OP_IDIV = 6;
	public static final short OP_IMOD = 7;
	public static final short OP_INEG = 8;
	public static final short OP_IEQN = 9;
	public static final short OP_ILEQ = 10;
	public static final short OP_IGEQ = 11;
	public static final short OP_ILTH = 12;
	public static final short OP_IGTH = 13;
	public static final short OP_IEQU = 14;
	public static final short OP_INEQ = 15;
	public static final short OP_ILAN = 16;
	public static final short OP_ILOR = 17;
	public static final short OP_JMP = 18;

	public static final short OP_PRINT = 24;

	private short opCode;
	private short argument;

	public AInstruction(short opCode, short argument) {
		this.opCode = opCode;
		this.argument = argument;
	}
	
	public static AInstruction read(DataInputStream input) throws IOException {
		short opCode = input.readShort();
		short argument = input.readShort();
		return new AInstruction(opCode, argument);
	}

	public void write(DataOutputStream output) throws IOException {
		output.writeShort(opCode);
		output.writeShort(argument);
	}

	@Override
	public String toString() {
		return String.format("(%s,%d)", opCodeStr[opCode], argument);
	}

}
