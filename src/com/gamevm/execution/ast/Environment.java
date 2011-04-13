package com.gamevm.execution.ast;

import java.util.List;
import java.util.Stack;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.execution.LoadedClass;
import com.gamevm.execution.RuntimeEnvironment;
import com.gamevm.execution.ast.tree.Statement;

public class Environment {
	
	private static RuntimeEnvironment system;
	
	private static Stack<Integer> frameSizes;
	
	private static Stack<Object> stack;
	private static Object returnRegister;
	
	private static Stack<ClassInstance> currentClass;
	
	private static List<LoadedClass> classPool;
	
	private static void loadClasses(ClassDefinition<Statement> mainClass) {
		// TODO
	}
	
	public static void initialize(RuntimeEnvironment system, ClassDefinition<Statement> mainClass) {
		Environment.system = system;
		frameSizes = new Stack<Integer>();
		stack = new Stack<Object>();
		returnRegister = null;
		currentClass = new Stack<ClassInstance>();
		loadClasses(mainClass);
	}
	
	public static void pushFrame() {
		
	}
	
	public static void popFrame() {
		int s = frameSizes.pop();
		for (int i = 0; i < s; i++) {
			stack.pop();
		}
	}
	
	public static int addVariable(Object initialValue) {
		int index = stack.size();
		stack.push(initialValue);
		int s = frameSizes.pop();
		frameSizes.push(s+1);
		return index;
	}
	
	public static void writeReturnRegister(Object value) {
		returnRegister = value;
	}
	
	public static <T> T callStaticMethod(int classIndex, int m, Object... parameters) {
		return null;
	}
	
	public static <T> T callMethod(ClassInstance thisClass, int m, Object... parameters) {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getValue(int variable) {
		return (T)stack.get(variable);
	}
	
	public static <T> void setValue(int variable, T value) {
		stack.set(variable, value);
	}
	
	public static <T> T getField(ClassInstance thisClass, int field) {
		if (thisClass != null)
			return thisClass.getValue(field);
		else
			return currentClass.peek().getValue(field);
	}
	
	public static <T> void setField(ClassInstance thisClass, int field, T value) {
		if (thisClass != null) {
			thisClass.setValue(field, value);
		} else {
			currentClass.peek().setValue(field, value);
		}
	}
	
	public static <T> T getStaticField(int classIndex, int field) {
		return classPool.get(classIndex).getValue(field);
	}
	
	public static <T> void setStaticField(int classIndex, int field, T value) {
		classPool.get(classIndex).setValue(field, value);
	}
	
	public static ClassDeclaration getClassInformation(int classIndex) {
		return classPool.get(classIndex).getClassInformation();
	}

}
