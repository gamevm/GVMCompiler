package com.gamevm.compiler.translator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.gamevm.compiler.assembly.CodeReader;
import com.gamevm.compiler.assembly.CodeWriter;
import com.gamevm.compiler.assembly.Instruction;
import com.gamevm.compiler.parser.ASTNode;

public class Code<I extends Instruction> {

	private static final int DEBUG_INFORMATION = 1;
	private static final int NO_DEBUG_INFORMATION = 0;

	private List<I> instructions;
	private int maxLocals;

	private Map<Instruction, ASTNode> debugInformation;
	private int pc;

	public Code(List<I> instructions,
			Map<Instruction, ASTNode> debugInformation, int maxLocals) {
		this.instructions = instructions;
		this.debugInformation = debugInformation;
		this.maxLocals = maxLocals;
		this.pc = 0;
	}

	public Code(DataInputStream input, CodeReader<I> reader, int codeSize)
			throws IOException {
		maxLocals = input.readInt();
		int debuggingFlag = input.readInt();
		instructions = new ArrayList<I>(codeSize);
		Map<Instruction, ASTNode> debugInfo = null;
		reader.open(input);
		for (int j = 0; j < codeSize; j++) {
			instructions.add(reader.readInstruction());
		}
		reader.close();
		if (debuggingFlag == DEBUG_INFORMATION) {
			// debugInfo = new HashMap<Instruction, ASTNode>();
			// for (int j = 0; j < codeSize; j++) {
			// debugInfo.put(instructions.get(j), readASTNode(input));
			// }
		}
	}

	public void write(DataOutputStream output, CodeWriter<I> writer)
			throws IOException {
		output.writeInt(getSize());
		if (getSize() > 0) {
			output.writeInt(getMaxLocals());
			output.writeInt(hasDebugInformation() ? DEBUG_INFORMATION
					: NO_DEBUG_INFORMATION);
			writer.open(output);
			for (I instr : getInstructions()) {
				writer.writeInstruction(instr);
			}
			writer.close();
			if (hasDebugInformation()) {
				// for (I instr : getInstructions()) {
				// writeASTNode(output, getDebugInformation(instr));
				// }
			}
		}
	}

	public List<I> getInstructions() {
		return instructions;
	}

	public I getInstruction() {
		return instructions.get(pc);
	}

	public ASTNode getDebugInformation(Instruction instruction) {
		if (debugInformation == null)
			return null;
		else
			return debugInformation.get(instruction);
	}

	public boolean hasDebugInformation() {
		return debugInformation != null;
	}

	public I nextInstruction() {
		pc++;
		return instructions.get(pc);
	}

	public boolean hasNextInstruction() {
		return pc < instructions.size() - 1;
	}

	public I jump(int target) {
		pc = target;
		return instructions.get(pc);
	}

	public int getSize() {
		return instructions.size();
	}

	public String toString(int ident) {
		StringBuilder b = new StringBuilder();
		for (I i : instructions) {
			b.append(i.toString(ident));
			b.append('\n');
		}
		return b.toString();
	}

	public String toString() {
		return toString(0);
	}

	public int getMaxLocals() {
		return maxLocals;
	}

	private void countLocalVariables(ASTNode n) {
		if (n.getType() == ASTNode.TYPE_VAR_DECL)
			maxLocals++;
		for (ASTNode c : n.getChildren()) {
			countLocalVariables(c);
		}
	}

	public static Code<ASTNode> getASTCode(ASTNode node) {
		List<ASTNode> list = Arrays.asList(node);
		return new Code<ASTNode>(list, null,
				node.countNodes(ASTNode.TYPE_VAR_DECL));
	}

	public static Code<ASTNode> getASTCode(List<ASTNode> nodes) {
		int maxLocals = 0;
		for (ASTNode n : nodes) {
			maxLocals += n.countNodes(ASTNode.TYPE_VAR_DECL);
		}

		return new Code<ASTNode>(nodes, null, maxLocals);
	}

}
