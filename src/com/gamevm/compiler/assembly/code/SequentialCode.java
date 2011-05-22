package com.gamevm.compiler.assembly.code;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


public abstract class SequentialCode<I> implements Code {
	
	private List<I> instructions;
	
	public SequentialCode() {
		instructions = new ArrayList<I>();
	}
	
	protected abstract void writeInstruction(I instruction, ObjectOutputStream out);
	
	protected abstract I readInstruction(ObjectInputStream in);
	
	protected abstract void writeCodeHeader(ObjectOutputStream out);
	
	protected abstract void readCodeHeader(ObjectInputStream in);
	
	public int getInstructionCount() {
		return instructions.size();
	}
	
	public final List<I> getInstructions() {
		return instructions;
	}

	@Override
	public void write(ObjectOutputStream out) throws IOException {
		out.writeInt(getInstructionCount());
		if (getInstructionCount() > 0) {
			writeCodeHeader(out);
			for (I instr : instructions) {
				writeInstruction(instr, out);
			}
		}
	}

	@Override
	public void read(ObjectInputStream in) throws IOException {
		int instructionCount = in.readInt();
		if (instructionCount > 0) {
			readCodeHeader(in);
			for (int i = 0; i < instructionCount; i++) {
				instructions.add(readInstruction(in));
			}
		}
	}

}
