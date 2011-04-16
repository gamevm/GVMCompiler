package com.gamevm.execution.ast;


public class CopyOfArrayInstance extends ClassInstance {
	
	private Object[] array;
	private Object defaultValue;
	
	private int[] dimensions;
	
	public CopyOfArrayInstance(Object defaultValue, int... dimensions) {
		super(ArrayClass.CLASS);
		this.defaultValue = defaultValue;
		this.dimensions = dimensions;
		createArray(0);
	}
	
	private Object[] createArray(int dimensionIndex) {
		Object[] result = new Object[dimensions[dimensionIndex]];
		Object v = (dimensionIndex == dimensions.length - 1) ? defaultValue : createArray(dimensionIndex+1);
		for (int i = 0; i < result.length; i++) {
			result[i] = v;
		}
		return result;
	}
	
	public Object get(int... indices) {
		Object[] a = array;
		for (int i = 0; i < indices.length-1; i++) {
			a = (Object[])a[indices[i]];
		}
		return a[indices[indices.length-1]];
	}
	
	public void set(int... indices) {
		
	}

}
