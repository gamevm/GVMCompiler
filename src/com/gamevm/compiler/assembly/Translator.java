package com.gamevm.compiler.assembly;


public abstract class Translator<Source extends Instruction, Target extends Instruction> {
	
	protected abstract Target[] generateCode(Source... src);
	
	public Code<Target> translate(Code<Source> src) {
		return new Code<Target>(generateCode(src.getInstructions()));
	}

}
