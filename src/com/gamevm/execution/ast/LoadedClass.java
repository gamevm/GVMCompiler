package com.gamevm.execution.ast;

import java.util.ArrayList;
import java.util.List;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.Field;
import com.gamevm.execution.ast.tree.Statement;

public class LoadedClass {
	
	private ClassDefinition<Statement> clazz;
	private Object[] staticFields;
	
	private int index;
	private int staticFieldCount;
	
	public LoadedClass(ClassDefinition<Statement> clazz, int index) {
		this.clazz = clazz;
		 staticFields = new Object[clazz.getFieldCount()];
		int i = 0;
		for (Field f : clazz.getDeclaration().getFields()) {
			if (f.isStatic()) {
				staticFields[i++] = f.getType().getDefaultValue();
				staticFieldCount++;
			}
		}
		this.index = index;
	}
	
	public int getStaticFieldCount() {
		return staticFieldCount;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(int field) {
		return (T)staticFields[field];
	}
	
	public <T> void setValue(int field, T value) {
		staticFields[field] = value;
	}
	
	public ClassDeclaration getClassInformation() {
		return clazz.getDeclaration();
	}
	
	public ClassDefinition<Statement> getDefinition() {
		return clazz;
	}
	
	public <T> T callNative(int index) {
		return null;
	}

	public int getIndex() {
		return index;
	}
	
	public boolean isNative() {
		return false;
	}

}
