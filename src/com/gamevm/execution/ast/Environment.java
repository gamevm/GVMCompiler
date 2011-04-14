package com.gamevm.execution.ast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.Type;
import com.gamevm.execution.RuntimeEnvironment;
import com.gamevm.execution.ast.tree.ReturnException;
import com.gamevm.execution.ast.tree.Statement;

public class Environment {
	
	private static RuntimeEnvironment system;
	
	private static Stack<Integer> frameSizes;
	
	private static Stack<Object> stack;
	private static Object returnRegister;
	
	private static Stack<ClassInstance> currentClass;
	
	private static List<LoadedClass> classPool;
	private static LoadedClass mainClass;
	
	private static void loadClass(File file) {
		
	}
	
	private static void loadClasses() {
		classPool.add(mainClass);
		for (Type t : Type.getRegisteredClasses()) {
			
		}
	}
	
	public static void initialize(RuntimeEnvironment system, ClassDefinition<Statement> mainClass) {
		Environment.system = system;
		frameSizes = new Stack<Integer>();
		stack = new Stack<Object>();
		returnRegister = null;
		currentClass = new Stack<ClassInstance>();
		classPool = new ArrayList<LoadedClass>();
		Environment.mainClass = new LoadedClass(mainClass, 0);
		loadClasses();
	}
	
	public static LoadedClass getMainClass() {
		return mainClass;
	}
	
	public static void pushFrame() {
		frameSizes.push(0);
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
	
	private static <T> T call(LoadedClass c, ClassInstance thisClass, int m, Object... parameters) {
		Collection<Statement> code = c.getDefinition().getImplementation(m).getInstructions();
		pushFrame();
		currentClass.push(thisClass);
		returnRegister = null;
		for (Object p : parameters) {
			addVariable(p);
		}
		try {
			for (Statement s : code) {
				s.execute();
			}
		} catch (ReturnException e) {
		}
		currentClass.pop();
		popFrame();
		return (T)returnRegister;
	}
	
	public static <T> T callStaticMethod(int classIndex, int m, Object... parameters) {
		LoadedClass c = classPool.get(classIndex);
		return call(c, null, m, parameters);
	}
	
	public static <T> T callMethod(ClassInstance thisClass, int m, Object... parameters) {
		LoadedClass c = thisClass.getLoadedClass();
		return call(c, thisClass, m, parameters);
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
