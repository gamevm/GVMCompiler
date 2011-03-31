package com.gamevm.ebc.execution;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;

import com.gamevm.ebc.bcfile.ClassFile;
import com.gamevm.ebc.bcfile.CodeAttribute;
import com.gamevm.ebc.bcfile.Method;

public class VirtualMachine {

	public static final int AALOAD = 50;
	public static final int AASTORE = 83;
	public static final int ACONST_NULL = 1;
	public static final int ALOAD = 25;
	public static final int ALOAD_0 = 42;
	public static final int ALOAD_1 = 43;
	public static final int ALOAD_2 = 44;
	public static final int ALOAD_3 = 45;
	public static final int ANEWARRAY = 189;
	public static final int ARETURN = 176;
	public static final int ARRAYLENGTH = 190;
	public static final int ASTORE = 58;
	public static final int ASTORE_0 = 75;
	public static final int ASTORE_1 = 76;
	public static final int ASTORE_2 = 77;
	public static final int ASTORE_3 = 78;
	public static final int ATHROW = 191;
	public static final int BALOAD = 51;
	public static final int BASTORE = 84;
	public static final int BIPUSH = 16;
	public static final int CALOAD = 52;
	public static final int CASTORE = 85;
	public static final int CHECKCAST = 192;
	public static final int D2F = 144;
	public static final int D2I = 142;
	public static final int D2L = 143;
	public static final int DADD = 99;
	public static final int DALOAD = 49;
	public static final int DASTORE = 82;
	public static final int DCMPG = 152;
	public static final int DCMPL = 151;
	public static final int DCONST_0 = 14;
	public static final int DCONST_1 = 15;
	public static final int DDIV = 111;
	public static final int DLOAD = 24;
	public static final int DLOAD_0 = 38;
	public static final int DLOAD_1 = 39;
	public static final int DLOAD_2 = 40;
	public static final int DLOAD_3 = 41;
	public static final int DMUL = 107;
	public static final int DNEG = 119;
	public static final int DREM = 115;
	public static final int DRETURN = 175;
	public static final int DSTORE = 57;
	public static final int DSTORE_0 = 71;
	public static final int DSTORE_1 = 72;
	public static final int DSTORE_2 = 73;
	public static final int DSTORE_3 = 74;
	public static final int DSUB = 103;
	public static final int DUP = 89;
	public static final int DUP_X1 = 90;
	public static final int DUP_X2 = 91;
	public static final int DUP2 = 92;
	public static final int DUP2_X1 = 93;
	public static final int DUP2_X2 = 94;
	public static final int F2D = 141;
	public static final int F2I = 139;
	public static final int F2L = 140;
	public static final int FADD = 98;
	public static final int FALOAD = 48;
	public static final int FASTORE = 81;
	public static final int FCMPG = 150;
	public static final int FCMPL = 149;
	public static final int FCONST_0 = 11;
	public static final int FCONST_1 = 12;
	public static final int FCONST_2 = 13;
	public static final int FDIV = 110;
	public static final int FLOAD = 23;
	public static final int FLOAD_0 = 34;
	public static final int FLOAD_1 = 35;
	public static final int FLOAD_2 = 36;
	public static final int FLOAD_3 = 37;
	public static final int FMUL = 106;
	public static final int FNEG = 118;
	public static final int FREM = 114;
	public static final int FRETURN = 174;
	public static final int FSTORE = 56;
	public static final int FSTORE_0 = 67;
	public static final int FSTORE_1 = 68;
	public static final int FSTORE_2 = 69;
	public static final int FSTORE_3 = 70;
	public static final int FSUB = 102;
	public static final int INVOKESPECIAL = 183;
	public static final int RETURN = 177;
	public static final int IADD = 96;
	public static final int ICONST_M1 = 2;
	public static final int ICONST_0 = 3;
	public static final int ICONST_1 = 4;
	public static final int ICONST_2 = 5;
	public static final int ICONST_3 = 6;
	public static final int ICONST_4 = 7;
	public static final int ICONST_5 = 8;
	public static final int ILOAD_0 = 26;
	public static final int ILOAD_1 = 27;
	public static final int ILOAD_2 = 28;
	public static final int ILOAD_3 = 29;
	public static final int IMUL = 104;
	public static final int ISTORE_0 = 59;
	public static final int ISTORE_1 = 60;
	public static final int ISTORE_2 = 61;
	public static final int ISTORE_3 = 62;

	private Stack<Frame> frames;
	private Stack<Object> operands;

	public VirtualMachine() {
		frames = new Stack<Frame>();
		operands = new Stack<Object>();
	}

	public void execute(ClassFile mainClass) {
		Method main = mainClass.getMethod("main");
		callMethod(main);
	
	}

	public void callMethod(Method m) {
		CodeAttribute code = m.getCode();
		Object[] localVariables = new Object[code.maxLocals];
		frames.push(new Frame(localVariables));
		executeCode(code.code);
		frames.pop();
	}

	private void executeCode(byte[] code) {
		int pc = 0;

		try {
			DataInputStream codeStream = new DataInputStream(
					new ByteArrayInputStream(code));
			int command = -1;
			Object[] localVars = frames.peek().getLocalVariables();
			while (true) {
				command = codeStream.readUnsignedByte();
				switch (command) {
				case ALOAD_0:
					operands.push(localVars[0]);
					break;
				case BIPUSH:
					operands.push((int)codeStream.readByte());
					break;
				case IADD:
					operands.push((Integer)operands.pop() + (Integer)operands.pop());
					break;
				case ICONST_M1:
					operands.push(-1);
					break;
				case ICONST_0:
					operands.push(0);
					break;
				case ICONST_1:
					operands.push(1);
					break;
				case ICONST_2:
					operands.push(2);
					break;
				case ICONST_3:
					operands.push(3);
					break;
				case ICONST_4:
					operands.push(4);
					break;
				case ICONST_5:
					operands.push(5);
					break;
				case ILOAD_0:
					operands.push(localVars[0]);
					break;
				case ILOAD_1:
					operands.push(localVars[1]);
					break;
				case ILOAD_2:
					operands.push(localVars[2]);
					break;
				case ILOAD_3:
					operands.push(localVars[3]);
					break;
				case IMUL:
					operands.push((Integer)operands.pop() * (Integer)operands.pop());
					break;
				case ISTORE_0:
					localVars[0] = operands.pop();
					break;
				case ISTORE_1:
					localVars[1] = operands.pop();
					break;
				case ISTORE_2:
					localVars[2] = operands.pop();
					break;
				case ISTORE_3:
					localVars[3] = operands.pop();
					break;
				case INVOKESPECIAL:
					int methodIndex = codeStream.readUnsignedShort();
					// ignore for now
					break;
				case RETURN:
					System.out.println(Arrays.toString(localVars));
					return;
				}
				
				
			}
			
			

		} catch (IOException ignore) {
		}

	}
}
