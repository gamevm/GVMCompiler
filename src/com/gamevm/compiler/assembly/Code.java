package com.gamevm.compiler.assembly;

import java.util.Arrays;
import java.util.Collection;

import com.gamevm.compiler.parser.ASTNode;
import com.gamevm.execution.NameTable;


public class Code<I extends Instruction> {
	
	private Collection<I> instructions;
	
	Code(Collection<I> instructions) {
		this.instructions = instructions;
	}
	
	public Collection<I> getInstructions() {
		return instructions;
	}
	
	public int getSize() {
		return instructions.size();
	}
	
	public String toString(int ident, NameTable names) {
		StringBuilder b = new StringBuilder();
		for (I i : instructions) {
			b.append(i.toString(ident));
			b.append('\n');
		}
		return b.toString();
	}
	
	public String toString(NameTable names) {
		return toString(0, names);
	}
	
	public static Code<ASTNode> getASTCode(ASTNode node) {
		return new Code<ASTNode>(Arrays.asList(node));
	}

}
