package com.gamevm.compiler.translator;

public class CodeSection {
	
	private int startIndex;
	private int endIndex;
	
	public CodeSection(int start, int end) {
		this.startIndex = start;
		this.endIndex = end;
	}
	
	public int getStartIndex() {
		return startIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public int getLength() {
		return endIndex - startIndex + 1;
	}

}
