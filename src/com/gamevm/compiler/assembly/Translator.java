package com.gamevm.compiler.assembly;

import java.util.Collection;

import com.gamevm.compiler.translator.TranslationException;


public abstract class Translator<Source extends Instruction, Target extends Instruction> {
	
	protected abstract Collection<Target> generateCode(Method m, Collection<Source> src) throws TranslationException;
	
	public Code<Target> translate(Method m, Code<Source> src)  throws TranslationException {
		return new Code<Target>(generateCode(m, src.getInstructions()));
	}

}
