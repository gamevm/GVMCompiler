package com.gamevm.compiler.translator;

import java.util.List;

import com.gamevm.compiler.assembly.Method;
import com.gamevm.compiler.assembly.code.Code;


public abstract class Translator<Source extends Code, Target extends Code> {
	
	public abstract List<TranslationException> getErrors();
	
	public abstract Target translate(Method m, Source src)  throws TranslationException;

}
