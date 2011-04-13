package com.gamevm.compiler.assembly;

import com.gamevm.compiler.translator.TranslationException;


public abstract class Translator<Source extends Instruction, Target extends Instruction> {
	
	protected abstract Target[] generateCode(Method m, Source... src) throws TranslationException;
	
	public Code<Target> translate(Method m, Code<Source> src)  throws TranslationException {
		return new Code<Target>(generateCode(m, src.getInstructions()));
	}

}
