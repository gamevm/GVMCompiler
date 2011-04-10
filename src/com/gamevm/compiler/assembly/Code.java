package com.gamevm.compiler.assembly;

import com.gamevm.compiler.parser.ASTNode;


public class Code<I extends Instruction> {
	
	private I[] instructions;
	
	Code(I[] instructions) {
		this.instructions = instructions;
	}
	
	I[] getInstructions() {
		return instructions;
	}
	
	public int getSize() {
		return instructions.length;
	}
	
	public String toString(int ident) {
		StringBuilder b = new StringBuilder();
		for (I i : instructions) {
			b.append(i.toString(ident));
			b.append('\n');
		}
		return b.toString();
	}
	
	@Override
	public String toString() {
		return toString(0);
	}
	
	public static Code<ASTNode> getASTCode(ASTNode node) {
		return new Code<ASTNode>(new ASTNode[] { node });
	}

}
