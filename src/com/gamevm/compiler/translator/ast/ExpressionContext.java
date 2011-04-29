package com.gamevm.compiler.translator.ast;

import com.gamevm.compiler.Type;

public class ExpressionContext {
	
	public boolean lvalue;
	public Type targetType;
	
	public ExpressionContext(boolean lvalue, Type targetType) {
		super();
		this.lvalue = lvalue;
		this.targetType = targetType;
	}

	public ExpressionContext(ExpressionContext c) {
		this.lvalue = c.lvalue;
		this.targetType = c.targetType;
	}
	
	public ExpressionContext setLValue(boolean on) {
		ExpressionContext c = new ExpressionContext(this);
		c.lvalue = on;
		return c;
	}
	
	public ExpressionContext setTargetType(Type t) {
		ExpressionContext c = new ExpressionContext(this);
		c.targetType = t;
		return c;
	}

}
