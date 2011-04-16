package com.gamevm.execution.ast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.CodeReader;
import com.gamevm.compiler.assembly.GClassLoader;
import com.gamevm.compiler.assembly.Instruction;
import com.gamevm.compiler.assembly.Type;
import com.gamevm.compiler.translator.Code;
import com.gamevm.compiler.translator.ast.ClassSymbol;
import com.gamevm.execution.RuntimeEnvironment;
import com.gamevm.execution.ast.tree.ReturnException;
import com.gamevm.execution.ast.tree.Statement;

public class Environment {
	
	private static Environment instance;
	private static Set<String> nativeClasses = new HashSet<String>();
	
	{
		nativeClasses.add("gc.String");
	}
	
	public static void initialize(Environment e) {
		instance = e;
	}
	
	public static Environment getInstance() {
		return instance;
	}
	
	private RuntimeEnvironment system;
	
	private Stack<Frame> stack;
	private Object returnRegister;
	
	private Stack<ClassInstance> currentClassInstances;
	private LoadedClass currentClass;
	
	
	
	private List<LoadedClass> classPool;
	private LoadedClass arrayClass;
	private LoadedClass mainClass;
	
	private boolean debugMode;
	
	private DebugHandler debugHandler;
	
	private Stack<Code<Statement>> currentCode;
	
	private GClassLoader loader;
	
	ClassInstance getThis() {
		if (currentClassInstances.size() > 0)
			return currentClassInstances.peek();
		else
			return null;
	}
	
	LoadedClass getCurrentClass() {
		return currentClass;
	}
	
	Object[] getLocals() {
		if (stack.size() > 0)
			return stack.peek().getLocals();
		else
			return new Object[0];
	}
	
	private void initializeClass(LoadedClass c) throws InterruptedException {
		Code<Statement> codeInfo = c.getDefinition().getStaticConstructor();
		call(c, codeInfo, null);
	}
	
	
	private void loadClass(Type t) throws FileNotFoundException, IOException, InterruptedException {
		
		
		CodeReader<Statement> reader = new ASTReader();
		ClassDefinition<Statement> c = loader.readDefinition(t.getName(), reader);
		loadClass(c);
	}
	
	private LoadedClass loadClass(ClassDefinition<Statement> c) throws InterruptedException, FileNotFoundException, IOException {
		final LoadedClass lc;
		if (nativeClasses.contains(c.getDeclaration().getName())) {
			
		} else {
			LoadedClass lc = new LoadedClass(c, classPool.size());
		}
		initializeClass(lc);
		classPool.add(lc);
		
		for (Type t : lc.getClassInformation().getImports()) {
			loadClass(t);
		}
		
		return lc;
	}
	
	private void loadClasses(ClassDefinition<Statement> mainClass) throws InterruptedException, FileNotFoundException, IOException {
		for (Type t : Type.IMPLICIT_IMPORTS) {
			loadClass(t);
		}
		this.mainClass = loadClass(mainClass);
	}
	
	
	
	public Environment(RuntimeEnvironment system, GClassLoader loader, ClassDefinition<Statement> mainClass, boolean debugMode) throws InterruptedException, FileNotFoundException, IOException {
		this.system = system;
		this.loader = loader;
		stack = new Stack<Frame>();
		returnRegister = null;
		currentClassInstances = new Stack<ClassInstance>();
		classPool = new ArrayList<LoadedClass>();
		currentCode = new Stack<Code<Statement>>();
		this.debugMode = debugMode;
		loadClasses(mainClass);
		
		arrayClass = new ArrayClass();
	}
	
	public LoadedClass getMainClass() {
		return mainClass;
	}
	
	public void pushFrame(int size, Object... parameters) {
		stack.push(new Frame(size, parameters));
	}
	
	public void popFrame() {
		stack.pop();
	}
	
	public void addVariable(Object initialValue) {
		stack.peek().addVariable(initialValue);
	}
	
	public void writeReturnRegister(Object value) {
		returnRegister = value;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T call(LoadedClass parentClass, Code<Statement> codeInfo, ClassInstance thisClass, Object... parameters) throws InterruptedException {
		currentClass = parentClass;
		
		currentCode.push(codeInfo);
		pushFrame(codeInfo.getMaxLocals() + parameters.length);
		currentClassInstances.push(thisClass);
		returnRegister = null;
		for (Object p : parameters) {
			addVariable(p);
		}
		try {
			for (Statement s : codeInfo.getInstructions()) {
				s.execute();
			}
		} catch (ReturnException e) {
		}
		currentCode.pop();
		currentClassInstances.pop();
		popFrame();
		return (T)returnRegister;
	}
	
	private <T> T call(LoadedClass c, ClassInstance thisClass, int m, Object... parameters) throws InterruptedException {
		if (c.getClassInformation().getMethod(m).getName().equals("<init>"))
			call(c, c.getDefinition().getImplicitConstructor(), thisClass, parameters);
		
		return call(c, c.getDefinition().getImplementation(m), thisClass, parameters);
	}
	
	public <T> T callStaticMethod(int classIndex, int m, Object... parameters) throws InterruptedException {
		LoadedClass c = getClass(classIndex);
		return call(c, null, m, parameters);
	}
	
	public <T> T callMethod(ClassInstance thisClass, int m, Object... parameters) throws InterruptedException {
		LoadedClass c = thisClass.getLoadedClass();
		return call(c, thisClass, m, parameters);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(int variable) {
		return (T)stack.peek().getVariable(variable);
	}
	
	public <T> void setValue(int variable, T value) {
		stack.peek().setVariable(variable, value);
	}
	
	public <T> T getField(ClassInstance thisClass, int field) {
		if (thisClass != null)
			return thisClass.getValue(field);
		else
			return currentClassInstances.peek().getValue(field);
	}
	
	public <T> void setField(ClassInstance thisClass, int field, T value) {
		if (thisClass != null) {
			thisClass.setValue(field, value);
		} else {
			currentClassInstances.peek().setValue(field, value);
		}
	}
	
	public LoadedClass getClass(int index) {
		if ((index & ClassSymbol.ARRAY_MASK) > 0) {
			return arrayClass;
		} else {
			return classPool.get(index);
		}
	}
	
	public <T> T getStaticField(int classIndex, int field) {
		return getClass(classIndex).getValue(field);
	}
	
	public <T> void setStaticField(int classIndex, int field, T value) {
		getClass(classIndex).setValue(field, value);
	}
	
	public ClassDeclaration getClassInformation(int classIndex) {
		return getClass(classIndex).getClassInformation();
	}
	
	public ClassInstance newInstance(int classIndex, int constructorIndex, Object... parameters) throws InterruptedException {
		LoadedClass classType = getClass(classIndex);
		ClassInstance instance = new ClassInstance(classType);
		call(classType, instance, constructorIndex, parameters);
		return instance;
	}
	
	public boolean isBreakPoint(Instruction s) {
		return debugMode;
	}
	
	public void continueExecution() {
		synchronized (currentDebugInstruction) {
			currentDebugInstruction.notify();
		}
	}
	
	public void setDebugHandler(DebugHandler d) {
		debugHandler = d;
	}
	
	private Instruction currentDebugInstruction;
	
	public void debug(Instruction i) {
		if (debugHandler != null)
			debugHandler.debug(i, currentCode.peek().getDebugInformation(i));
		currentDebugInstruction = i;
	}

}
