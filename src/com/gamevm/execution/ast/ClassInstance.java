package com.gamevm.execution.ast;

import java.util.ArrayList;
import java.util.List;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.Field;

public class ClassInstance {
	
	private ClassDeclaration clazz;
	
	private Object[] fields;
	
	public ClassInstance(ClassDeclaration clazz) {
		this.clazz = clazz;
		List<Object> tmpFields = new ArrayList<Object>();
		for (Field f : clazz.getFields()) {
			if (!f.isStatic())
				tmpFields.add(f.getType().getDefaultValue());
		}
		fields = tmpFields.toArray(new Object[tmpFields.size()]);
	}
	
	public ClassDeclaration getClassDeclaration() {
		return clazz;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(int field) {
		return (T)fields[field];
	}
	
	public <T> void setValue(int field, T value) {
		fields[field] = value;
	}

}
