package com.gamevm.compiler.translator;

import com.gamevm.compiler.parser.old.Expression;
import com.gamevm.compiler.parser.old.Statement;

public interface Translator {

	public void processStatement(Statement s);
	
	public void processExpression(Expression e);
	
}
