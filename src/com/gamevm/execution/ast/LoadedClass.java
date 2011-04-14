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
	
	public LoadedClass(ClassDefinition<Statement> clazz, int index) {
		this.clazz = clazz;
		List<Object> tmpFields = new ArrayList<Object>();
		for (Field f : clazz.getDeclaration().getFields()) {
			if (f.isStatic())
				tmpFields.add(f.getType().getDefaultValue());
		}
		staticFields = tmpFields.toArray(new Object[tmpFields.size()]);
		this.index = index;
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

	public int getIndex() {
		return index;
	}
}
