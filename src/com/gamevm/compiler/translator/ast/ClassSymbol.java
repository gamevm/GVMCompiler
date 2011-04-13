package com.gamevm.compiler.translator.ast;

import com.gamevm.compiler.assembly.ClassDeclaration;

public class ClassSymbol {
	
	private ClassDeclaration declaration;
	private int index;
	
	public ClassSymbol(int index, ClassDeclaration declaration) {
		this.declaration = declaration;
		this.index = index;
	}

	public ClassDeclaration getDeclaration() {
		return declaration;
	}

	public int getIndex() {
		return index;
	}
	
	public String getName() {
		return declaration.getName();
	}
	
	

}
