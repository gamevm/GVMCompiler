package com.gamevm.compiler.translator;

import java.util.List;
import java.util.Map;

import com.gamevm.compiler.assembly.Instruction;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.compiler.parser.ASTNode;


public abstract class Translator<Source extends Instruction, Target extends Instruction> {
	
	protected abstract List<Target> generateCode(Method m, List<Source> src) throws TranslationException;
	
	protected abstract Map<Instruction, ASTNode> getDebugInformation();
	
	public Code<Target> translate(Method m, Code<Source> src)  throws TranslationException {
		return new Code<Target>(generateCode(m, src.getInstructions()), getDebugInformation(), src.getMaxLocals());
	}

}
