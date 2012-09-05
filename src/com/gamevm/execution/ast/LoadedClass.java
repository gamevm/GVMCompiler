package com.gamevm.execution.ast;

import java.util.HashMap;
import java.util.Map;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.Field;
import com.gamevm.compiler.assembly.code.ExecutableTreeCode;

public class LoadedClass {
	
	protected static Map<Type, Class<?>> mapping = new HashMap<Type, Class<?>>();
	
	static {
		mapping.put(Type.VOID, Void.class);
		mapping.put(Type.BOOLEAN, Boolean.class);
		mapping.put(Type.BYTE, Byte.class);
		mapping.put(Type.CHAR, Character.class);
		mapping.put(Type.SHORT, Short.class);
		mapping.put(Type.INT, Integer.class);
		mapping.put(Type.LONG, Long.class);
		mapping.put(Type.FLOAT, Float.class);
		mapping.put(Type.DOUBLE, Double.class);
	}
	
	private ClassDefinition<ExecutableTreeCode> clazz;
	private Object[] staticFields;
	
	private int index;
	private int staticFieldCount;
	
	public LoadedClass(ClassDefinition<ExecutableTreeCode> clazz, int index) {
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
		
		mapping.put(clazz.getDeclaration().getType(), ClassInstance.class);
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
	
	public ClassDefinition<ExecutableTreeCode> getDefinition() {
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
