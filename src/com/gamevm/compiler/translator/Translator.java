package com.gamevm.compiler.translator;

import com.gamevm.compiler.parser.Expression;
import com.gamevm.compiler.parser.Statement;

public interface Translator {

	public void processStatement(Statement s);
	
	public void processExpression(Expression e);
	
}
