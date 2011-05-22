package com.gamevm.execution.ast;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.Field;

public class ClassInstance {
	
	private LoadedClass clazz;
	
	private Object[] fields;
	
	public ClassInstance(LoadedClass clazz) {
		this.clazz = clazz;
		fields = new Object[clazz.getDefinition().getFieldCount()];
		int i = 0;
		for (Field f : clazz.getClassInformation().getFields()) {
			if (!f.isStatic())
				fields[i++] = f.getType().getDefaultValue();
		}
	}
	
	public ClassDeclaration getClassDeclaration() {
		return clazz.getClassInformation();
	}
	
	public LoadedClass getLoadedClass() {
		return clazz;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(int field) {
		return (T)fields[field];
	}
	
	public <T> void setValue(int field, T value) {
		fields[field] = value;
	}
	
	public <T> T callNative(int index) {
		return null;
	}

}
