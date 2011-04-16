package com.gamevm.execution.ast;

import java.util.Arrays;


public class ArrayInstance extends ClassInstance {
	
	private static final int FIELD_LENGTH = 0;
	
	private Object[] array;
	private Object defaultValue;
	
	private int length;
	
	public ArrayInstance(Object defaultValue, int size) {
		super(ArrayClass.CLASS);
		this.defaultValue = defaultValue;
		this.length = size;
		array = new Object[size];
	}
	
	public Object get(int i) {
		return array[i];
	}
	
	public void set(int i, Object value) {
		array[i] = value;
	}
	
	@Override
	public <T> T getValue(int field) {
		switch (field) {
		case FIELD_LENGTH:
			return (T)Integer.valueOf(length);
		}
		return null;
	}
	
	public <T> void setValue(int field, T value) {
		switch (field) {
		case FIELD_LENGTH:
			break;
		}
	};
	
	@Override
	public String toString() {
		return Arrays.toString(array);
	}

}
