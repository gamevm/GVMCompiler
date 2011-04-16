package com.gamevm.compiler.translator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.gamevm.compiler.assembly.Instruction;
import com.gamevm.compiler.parser.ASTNode;


public class Code<I extends Instruction> {
	
	private List<I> instructions;
	private int maxLocals;
	
	private Map<Instruction, ASTNode> debugInformation;
	private int pc;
	
	
	public Code(List<I> instructions, Map<Instruction, ASTNode> debugInformation, int maxLocals) {
		this.instructions = instructions;
		this.debugInformation = debugInformation;
		this.maxLocals = maxLocals;
		this.pc = 0;
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
		return new Code<ASTNode>(list, null, node.countNodes(ASTNode.TYPE_VAR_DECL));
	}
	
	public static Code<ASTNode> getASTCode(List<ASTNode> nodes) {
		int maxLocals = 0;
		for (ASTNode n : nodes) {
			maxLocals += n.countNodes(ASTNode.TYPE_VAR_DECL);
		}
		
		return new Code<ASTNode>(nodes, null, maxLocals);
	}

}
