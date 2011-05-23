package com.gamevm.execution.ast.builtin;

import java.util.Arrays;

import com.gamevm.compiler.assembly.runtime.RuntimeClasses;
import com.gamevm.execution.ast.ClassInstance;


public class ArrayInstance extends ClassInstance {
	
	private Object[] array;
	//private Object defaultValue;
	
	private int length;
	
	public ArrayInstance(Object[] javaArray) {
		super(ArrayClass.CLASS);
		this.length = javaArray.length;
		array = javaArray;
	}
	
	public ArrayInstance(Object defaultValue, int size) {
		super(ArrayClass.CLASS);
		//this.defaultValue = defaultValue;
		this.length = size;
		array = new Object[size];
		Arrays.fill(array, defaultValue);
	}
	
	public Object get(int i) {
		return array[i];
	}
	
	public void set(int i, Object value) {
		array[i] = value;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getValue(int field) {
		switch (field) {
		case RuntimeClasses.FIELD_ARRAY_LENGTH:
			return (T)Integer.valueOf(length);
		}
		return null;
	}
	
	public <T> void setValue(int field, T value) {
		switch (field) {
		case RuntimeClasses.FIELD_ARRAY_LENGTH:
			break;
		}
	};
	
	@Override
	public String toString() {
		return Arrays.toString(array);
	}

}
