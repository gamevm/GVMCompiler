package com.gamevm.execution.ast;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.Field;
import com.gamevm.execution.ast.tree.TreeCodeInstruction;

public class LoadedClass {
	
	private ClassDefinition<TreeCodeInstruction> clazz;
	private Object[] staticFields;
	
	private int index;
	private int staticFieldCount;
	
	public LoadedClass(ClassDefinition<TreeCodeInstruction> clazz, int index) {
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
	
	public ClassDefinition<TreeCodeInstruction> getDefinition() {
		return clazz;
	}
	
	public <T> T callNative(int index, ClassInstance thisClass, Object... parameters) {
		return null;
	}

	public int getIndex() {
		return index;
	}
	
	void setIndex(int index) {
		this.index = index;
	}
	
	public boolean isNative() {
		return false;
	}

}
