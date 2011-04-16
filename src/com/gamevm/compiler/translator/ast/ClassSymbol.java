package com.gamevm.compiler.translator.ast;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.Type;

public class ClassSymbol {
	
	public static final int ARRAY_MASK = 0x80000000;
	public static final int PRIMITIVE_TYPE_MASK = 0x7F000000;
	
	
	
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
	
	public static int getIndex(Type primitive) {
		return (primitive.ordinal() << 24) | ARRAY_MASK;
	}

}
