package com.gamevm.compiler.translator;

import java.util.List;

import com.gamevm.compiler.Type;

public interface Expression<I> {
	
	public Type getType();
	
	public void setType(Type t);
	
	public List<I> getInstructions();

}
