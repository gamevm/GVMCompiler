package com.gamevm.execution.ast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.ClassFileHeader;
import com.gamevm.compiler.assembly.Instruction;
import com.gamevm.compiler.assembly.code.Code;
import com.gamevm.compiler.assembly.code.ExecutableTreeCode;
import com.gamevm.compiler.assembly.loader.GBCDirectoryLoader;
import com.gamevm.compiler.assembly.loader.Loader;
import com.gamevm.compiler.translator.ast.ClassSymbol;
import com.gamevm.execution.RuntimeEnvironment;
import com.gamevm.execution.ast.builtin.ArrayClass;
import com.gamevm.execution.ast.builtin.StringClass;
import com.gamevm.execution.ast.builtin.SystemClass;
import com.gamevm.execution.ast.tree.CodeNode;
import com.gamevm.execution.ast.tree.ReturnException;
import com.gamevm.execution.ast.tree.Statement;

public class Environment {
	
	public static ClassFileHeader FILE_HEADER = new ClassFileHeader(1, Code.CODE_TREE);

	private static Environment instance;
	private static Map<String, LoadedClass> nativeClasses = new HashMap<String, LoadedClass>();

	{
		nativeClasses.put("gc.String", StringClass.CLASS);
		nativeClasses.put("gc.System", SystemClass.CLASS);
	}

	private static void initialize(Environment e) {
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

	private HashMap<String, LoadedClass> classMap;
	private List<LoadedClass> classPool;
	private LoadedClass arrayClass;
	private LoadedClass mainClass;

	private boolean debugMode;

	private DebugHandler debugHandler;

	private Stack<ExecutableTreeCode> currentCode;

	private Loader loader;

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

	/**
	 * Invokes the static constructor on the given loaded class.
	 * @param c the class to initialize
	 * @throws InterruptedException
	 */
	private void initializeClass(LoadedClass c) throws InterruptedException {
		System.out.format("Initializing class %s ...\n", c.getClassInformation().getName());
		ExecutableTreeCode codeInfo = c.getDefinition().getStaticConstructor();
		if (codeInfo.getRoot() != null)
			call(c, codeInfo, null);
	}

	private void loadClass(Type t) throws FileNotFoundException, IOException, InterruptedException {	
		LoadedClass lc = classMap.get(t.getName());
		if (lc == null) {
			System.out.format("Loading class %s ...\n", t.getName());
			lc = nativeClasses.get(t.getName());
			if (lc == null) {
				ClassDefinition<ExecutableTreeCode> c = loader.readDefinition(t.getName());
				createClass(c);
			} else {
				registerLoadedClass(lc);
			}
		}
	}

	private LoadedClass createClass(ClassDefinition<ExecutableTreeCode> c) throws InterruptedException, FileNotFoundException,
			IOException {
		LoadedClass lc = classMap.get(c.getDeclaration().getName());
		if (lc != null) {
			return lc;
		}
		
		System.out.format("Creating class %s ...\n", c.getDeclaration().getName());

		lc = new LoadedClass(c, classPool.size());
		initializeClass(lc);
		registerLoadedClass(lc);

		return lc;
	}

	private void registerLoadedClass(LoadedClass lc) throws FileNotFoundException, IOException, InterruptedException {
		System.out.format("Registering class %s ...\n", lc.getClassInformation().getName());
		lc.setIndex(classPool.size());
		classPool.add(lc);
		classMap.put(lc.getClassInformation().getName(), lc);

		for (Type t : lc.getClassInformation().getImports()) {
			loadClass(t);
		}
	}

	private void loadClasses(ClassDefinition<ExecutableTreeCode> mainClass) throws InterruptedException, FileNotFoundException,
			IOException {
		for (Type t : Type.IMPLICIT_IMPORTS) {
			LoadedClass lc = classMap.get(t.getName());
			if (lc == null) {
				lc = nativeClasses.get(t.getName());
				registerLoadedClass(lc);
			}
		}
		this.mainClass = createClass(mainClass);
	}

	public Environment(RuntimeEnvironment system, Loader loader, ClassDefinition<ExecutableTreeCode> mainClass,
			boolean debugMode) throws InterruptedException, FileNotFoundException, IOException {
		initialize(this);
		
		this.system = system;
		this.loader = loader;
		stack = new Stack<Frame>();
		returnRegister = null;
		currentClassInstances = new Stack<ClassInstance>();
		classPool = new ArrayList<LoadedClass>();
		classMap = new HashMap<String, LoadedClass>();
		currentCode = new Stack<ExecutableTreeCode>();
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
	private <T> T call(LoadedClass parentClass, ExecutableTreeCode codeInfo, ClassInstance thisClass, Object... parameters)
			throws InterruptedException {
		currentClass = parentClass;

		currentCode.push(codeInfo);
		pushFrame(codeInfo.getRoot().getMaxLocals() + parameters.length);
		currentClassInstances.push(thisClass);
		returnRegister = null;
		for (Object p : parameters) {
			addVariable(p);
		}
		try {
			((Statement)codeInfo.getRoot()).execute();
		} catch (ReturnException e) {
		}
		currentCode.pop();
		currentClassInstances.pop();
		popFrame();
		return (T) returnRegister;
	}

	private <T> T call(LoadedClass c, ClassInstance thisClass, int m, Object... parameters) throws InterruptedException {
		if (c.isNative()) {
			return c.callNative(m, thisClass, parameters);
		} else {
			if (c.getClassInformation().getMethod(m).getName().equals("<init>") && c.getDefinition().getImplicitConstructor() != null)
				call(c, c.getDefinition().getImplicitConstructor(), thisClass, parameters);

			return call(c, c.getDefinition().getImplementation(m), thisClass, parameters);
		}
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
		return (T) stack.peek().getVariable(variable);
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
		if ((index & ClassSymbol.ARRAY_MASK) != 0) {
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

	public ClassInstance newInstance(int classIndex, int constructorIndex, Object... parameters)
			throws InterruptedException {
		LoadedClass classType = getClass(classIndex);
		ClassInstance instance = new ClassInstance(classType);
		call(classType, instance, constructorIndex, parameters);
		return instance;
	}

	public boolean isBreakPoint(CodeNode s) {
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

	public void debug(CodeNode i) {
//		if (debugHandler != null)
//			debugHandler.debug(i, currentCode.peek().getDebugInformation(i));
//		currentDebugInstruction = i;
	}

}
