package com.gamevm.execution.ast;

import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.translator.ast.ClassSymbol;
import com.gamevm.compiler.translator.ast.SymbolTable;
import com.gamevm.execution.ast.tree.Statement;

public class ArrayClass extends LoadedClass {
	
	public static final ArrayClass CLASS = new ArrayClass();

	public ArrayClass() {
		super(new ClassDefinition<Statement>(SymbolTable.ARRAY_DECLARATION, null, null, null), ClassSymbol.ARRAY_MASK);
	}

	@Override
	public boolean isNative() {
		return true;
	}
}
