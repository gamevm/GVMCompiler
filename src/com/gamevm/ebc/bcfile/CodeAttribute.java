package com.gamevm.ebc.bcfile;

public class CodeAttribute extends Attribute {
	
	public static class Exception {
		public int startPc;
		public int endPc;
		public int handlerPc;
		public int catchType;
		
		public Exception(int startPc, int endPc, int handlerPc, int catchType) {
			super();
			this.startPc = startPc;
			this.endPc = endPc;
			this.handlerPc = handlerPc;
			this.catchType = catchType;
		}
		
		
	}
	
	public int maxStack;
	public int maxLocals;
	
	public byte[] code;
	public Exception[] exceptionTable;
	
	public CodeAttribute(int maxStack, int maxLocals, byte[] code,
			Exception[] exceptionTable) {
		super();
		this.maxStack = maxStack;
		this.maxLocals = maxLocals;
		this.code = code;
		this.exceptionTable = exceptionTable;
	}
	
	
	

}
