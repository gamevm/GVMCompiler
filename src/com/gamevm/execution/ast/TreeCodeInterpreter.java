package com.gamevm.execution.ast;

import java.lang.reflect.Modifier;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.GClassLoader;
import com.gamevm.compiler.assembly.code.ExecutableTreeCode;
import com.gamevm.execution.InterpretationListener;
import com.gamevm.execution.Interpreter;
import com.gamevm.execution.RuntimeEnvironment;
import com.gamevm.execution.ast.builtin.ArrayInstance;
import com.gamevm.execution.ast.builtin.StringInstance;

public class TreeCodeInterpreter extends Interpreter<ExecutableTreeCode> {

	private boolean debugMode;
	private DebugHandler debugHandler;
	
	private Thread thread;
	private InterpretationListener listener;
	
	public TreeCodeInterpreter(RuntimeEnvironment system) {
		super(system);
		debugMode = false;
	}
	
	public void setDebugMode(boolean on, DebugHandler handler) {
		this.debugMode = on;
		this.debugHandler = handler;
	}
	
	public void continueExecution() {
		Environment.getInstance().continueExecution();
	}
	
	public void abortExecution() {
		thread.interrupt();
	}
	
	@Override
	public int execute(final ClassDefinition<ExecutableTreeCode> mainClass, final String[] args, InterpretationListener l, GClassLoader classLoader)
			throws Exception {
		listener = l;
		Environment env = new Environment(system, classLoader, mainClass, debugMode);
		Environment.initialize(env);
		Environment.getInstance().setDebugHandler(debugHandler);
		final int mainIndex = mainClass.getDeclaration().getMethod(Modifier.PUBLIC, true, "main", Type.getType("gc.String[]"));
		
		thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					
					StringInstance[] arguments = new StringInstance[args.length];
					for (int i = 0; i < args.length; i++) {
						arguments[i] = new StringInstance(args[i]);
					}
					
					Environment.getInstance().callStaticMethod(Environment.getInstance().getMainClass().getIndex(), mainIndex, new ArrayInstance(arguments));
				} catch (InterruptedException e) {
				}
			
				if (listener != null)
					listener.finished();
			}
		});
		thread.start();
		return 0;
	}

	
	
	

//	private Stack<Map<String, Address>> localVariables;
//
//	private Map<String, LoadedClass> classPool;
//
//	private Stack<ClassInstance> classStack;
//
//	private boolean returnBreak;
//	private Object returnRegister;
//
//	private class LoadedClass {
//
//		private ClassDefinition<ASTNode> classDef;
//		private Map<String, Collection<Integer>> classMethodIndex;
//		private Map<String, Collection<Integer>> instanceMethodIndex;
//
//		private Map<String, Address> classFields;
//
//		public LoadedClass(ClassDefinition<ASTNode> classDef)
//				throws CompilationException {
//			super();
//			this.classDef = classDef;
//			this.classMethodIndex = new HashMap<String, Collection<Integer>>();
//			this.instanceMethodIndex = new HashMap<String, Collection<Integer>>();
//
//			for (int i = 0; i < classDef.getMethodCount(); i++) {
//				final Method m = classDef.getMethod(i);
//				if (m.isStatic()) {
//					Collection<Integer> indices = classMethodIndex.get(m
//							.getName());
//					if (indices == null) {
//						indices = new LinkedList<Integer>();
//						classMethodIndex.put(m.getName(), indices);
//					}
//					indices.add(i);
//				} else {
//					Collection<Integer> indices = instanceMethodIndex.get(m
//							.getName());
//					if (indices == null) {
//						indices = new LinkedList<Integer>();
//						instanceMethodIndex.put(m.getName(), indices);
//					}
//					indices.add(i);
//				}
//			}
//
//			this.classFields = new HashMap<String, Address>();
//			for (int i = 0; i < classDef.getFieldCount(); i++) {
//				final Field f = classDef.getField(i);
//				if (f.isStatic()) {
//					Code<ASTNode> fieldInit = classDef
//							.getFieldInitialization(i);
//					Object val = f.getType().getDefaultValue();
//					if (fieldInit != null) {
//						val = execute(fieldInit.getInstructions()[0]);
//					}
//					classFields.put(f.getName(), new Address(f.getType(), val));
//				}
//			}
//		}
//
//		public Collection<Integer> getMethodIndices(String name,
//				boolean isStatic) {
//			if (isStatic) {
//				return classMethodIndex.get(name);
//			} else {
//				return instanceMethodIndex.get(name);
//			}
//		}
//
//		public Address getValue(String field) {
//			return classFields.get(field);
//		}
//
//	}
//
//	public ASTInterpreter(RuntimeEnvironment system) {
//		super(system);
//		localVariables = new Stack<Map<String, Address>>();
//		classStack = new Stack<ClassInstance>();
//		classPool = new HashMap<String, LoadedClass>();
//
//	}
//
//	public boolean isDeclared(String variable) {
//		for (int i = localVariables.size() - 1; i >= 0; i--) {
//			if (localVariables.get(i).containsKey(variable)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public Address getVariable(ClassInstance thisClass, String variable)
//			throws CompilationException {
//		for (int i = localVariables.size() - 1; i >= 0; i--) {
//			if (localVariables.get(i).containsKey(variable))
//				return localVariables.get(i).get(variable);
//		}
//
//		return thisClass.getValue(variable);
//	}
//
//	// public Address getAddress(ClassInstance thisClass, String name,
//	// boolean fieldsOnly) throws CompilationException {
//	// if (name.contains(".")) {
//	// // A reference to a field of this or another class.
//	// String[] accessPath = name.split(".");
//	// ClassInstance currentClass = thisClass;
//	// for (int i = 0; i < accessPath.length; i++) {
//	// if (i < accessPath.length - 1) {
//	// currentClass = (ClassInstance) getAddress(currentClass,
//	// accessPath[i], true).getValue();
//	// } else {
//	// return getAddress(currentClass, accessPath[i], true);
//	// }
//	// }
//	// return null;
//	// } else {
//	// if (name.equals("this"))
//	// return new Address(thisClass.getClassDeclaration().getType(),
//	// thisClass);
//	// // A local variable or field of this class.
//	// if (fieldsOnly) {
//	// return thisClass.getValue(name);
//	// } else {
//	// Address result = getVariable(name);
//	// if (result == null) {
//	// thisClass.getValue(name);
//	// }
//	// return result;
//	// }
//	// }
//	// }
//
//	public Object callMethod(ASTNode methodCall, Method m, Code<ASTNode> code)
//			throws CompilationException {
//		localVariables.push(new HashMap<String, Address>());
//
//		for (int i = 0; i < m.getParameters().length; i++) {
//			localVariables.peek().put(
//					m.getParameters()[i].getName(),
//					new Address(m.getParameters()[i].getType(),
//							execute(methodCall.getChildAt(i + 1))));
//		}
//
//		for (ASTNode i : code.getInstructions()[0].getChildren()) {
//			execute(i);
//			if (returnBreak)
//				break;
//		}
//		localVariables.pop();
//		returnBreak = false;
//		return returnRegister;
//	}
//
//	private int getTypeClassification(Class<?> c) {
//		if (c.equals(Byte.class)) {
//			return 0;
//		} else if (c.equals(Short.class)) {
//			return 1;
//		} else if (c.equals(Integer.class)) {
//			return 2;
//		} else if (c.equals(Long.class)) {
//			return 3;
//		} else if (c.equals(Float.class)) {
//			return 4;
//		} else if (c.equals(Double.class)) {
//			return 5;
//		}
//		return -1;
//	}
//
//	private int getTypeClassification(Object a, Object b) {
//		int aC = getTypeClassification(a.getClass());
//		int bC = getTypeClassification(b.getClass());
//		return Math.max(aC, bC);
//	}
//
//	private Object performAdd(ASTNode node) throws CompilationException {
//		Object a = node.getChildAt(0).getValue();
//		Object b = node.getChildAt(1).getValue();
//		int tC = getTypeClassification(a, b);
//		node.setValueType(Type.getPrimitiveType(tC));
//		switch (tC) {
//		case 0:
//			return (Byte) a + (Byte) b;
//		case 1:
//			return (Short) a + (Short) b;
//		case 2:
//			return (Integer) a + (Integer) b;
//		case 3:
//			return (Long) a + (Long) b;
//		case 4:
//			return (Float) a + (Float) b;
//		case 5:
//			return (Double) a + (Double) b;
//		}
//		throw new CompilationException(
//				"Operator + is not defined for non numeric types", node);
//	}
//
//	private Object performSub(ASTNode node) throws CompilationException {
//		Object a = node.getChildAt(0).getValue();
//		Object b = node.getChildAt(1).getValue();
//		int tC = getTypeClassification(a, b);
//		node.setValueType(Type.getPrimitiveType(tC));
//		switch (tC) {
//		case 0:
//			return (Byte) a - (Byte) b;
//		case 1:
//			return (Short) a - (Short) b;
//		case 2:
//			return (Integer) a - (Integer) b;
//		case 3:
//			return (Long) a - (Long) b;
//		case 4:
//			return (Float) a - (Float) b;
//		case 5:
//			return (Double) a - (Double) b;
//		}
//		throw new CompilationException(
//				"Operator - is not defined for non numeric types", node);
//	}
//
//	private Object performMult(ASTNode node) throws CompilationException {
//		Object a = node.getChildAt(0).getValue();
//		Object b = node.getChildAt(1).getValue();
//		int tC = getTypeClassification(a, b);
//		node.setValueType(Type.getPrimitiveType(tC));
//		switch (tC) {
//		case 0:
//			return (Byte) a * (Byte) b;
//		case 1:
//			return (Short) a * (Short) b;
//		case 2:
//			return (Integer) a * (Integer) b;
//		case 3:
//			return (Long) a * (Long) b;
//		case 4:
//			return (Float) a * (Float) b;
//		case 5:
//			return (Double) a * (Double) b;
//		}
//		throw new CompilationException(
//				"Operator * is not defined for non numeric types", node);
//	}
//
//	private Object performDiv(ASTNode node) throws CompilationException {
//		Object a = node.getChildAt(0).getValue();
//		Object b = node.getChildAt(1).getValue();
//		int tC = getTypeClassification(a, b);
//		node.setValueType(Type.getPrimitiveType(tC));
//		switch (tC) {
//		case 0:
//			return (Byte) a / (Byte) b;
//		case 1:
//			return (Short) a / (Short) b;
//		case 2:
//			return (Integer) a / (Integer) b;
//		case 3:
//			return (Long) a / (Long) b;
//		case 4:
//			return (Float) a / (Float) b;
//		case 5:
//			return (Double) a / (Double) b;
//		}
//		throw new CompilationException(
//				"Operator / is not defined for non numeric types", node);
//	}
//
//	private Object performMod(ASTNode node) throws CompilationException {
//		Object a = node.getChildAt(0).getValue();
//		Object b = node.getChildAt(1).getValue();
//		int tC = getTypeClassification(a, b);
//		node.setValueType(Type.getPrimitiveType(tC));
//		switch (tC) {
//		case 0:
//			return (Byte) a % (Byte) b;
//		case 1:
//			return (Short) a % (Short) b;
//		case 2:
//			return (Integer) a % (Integer) b;
//		case 3:
//			return (Long) a % (Long) b;
//		case 4:
//			return (Float) a % (Float) b;
//		case 5:
//			return (Double) a % (Double) b;
//		}
//		throw new CompilationException(
//				"Operator % is not defined for non numeric types", node);
//	}
//
//	private Object performNeg(ASTNode node) throws CompilationException {
//		Object a = node.getChildAt(0).getValue();
//		int tC = getTypeClassification(a.getClass());
//		node.setValueType(Type.getPrimitiveType(tC));
//		switch (tC) {
//		case 0:
//			return -(Byte) a;
//		case 1:
//			return -(Short) a;
//		case 2:
//			return -(Integer) a;
//		case 3:
//			return -(Long) a;
//		case 4:
//			return -(Float) a;
//		case 5:
//			return -(Double) a;
//		}
//		throw new CompilationException(
//				"Operator - is not defined for non numeric types", node);
//	}
//
//	public Object execute(ASTNode instruction) throws CompilationException {
//		final ASTNode body;
//		final ASTNode condition;
//		final String name;
//		final Comparable<Object> a;
//		final Comparable<Object> b;
//		final Object left;
//		final Object right;
//
//		final ASTNode a0;
//		final ASTNode a1;
//
//		final Object objA0;
//		final Object objA1;
//
//		switch (instruction.getType()) {
//		case ASTNode.TYPE_BLOCK:
//			localVariables.push(new HashMap<String, Address>());
//			for (ASTNode i : instruction.getChildren()) {
//				execute(i);
//				if (returnBreak)
//					break;
//			}
//			localVariables.pop();
//			return null;
//		case ASTNode.TYPE_WHILE_LOOP:
//			condition = instruction.getChildAt(0);
//			body = instruction.getChildAt(1);
//			while ((Boolean) execute(condition)) {
//				execute(body);
//				if (returnBreak)
//					break;
//			}
//			return null;
//		case ASTNode.TYPE_FOR_LOOP:
//			ASTNode initialization = instruction.getChildAt(0);
//			condition = instruction.getChildAt(1);
//			body = instruction.getChildAt(instruction.getChildCount() - 1);
//
//			execute(initialization);
//			while ((Boolean) execute(condition)) {
//				execute(body);
//				if (returnBreak)
//					break;
//				for (int i = 2; i < instruction.getChildCount() - 1; i++) {
//					execute(instruction.getChildAt(i));
//				}
//			}
//			return null;
//		case ASTNode.TYPE_IF:
//			condition = instruction.getChildAt(0);
//			body = instruction.getChildAt(1);
//			if (condition == null || (Boolean) execute(condition)) {
//				execute(body);
//				return true;
//			} else {
//				for (int i = 2; i < instruction.getChildCount(); i++) {
//					boolean result = (Boolean) execute(instruction
//							.getChildAt(i));
//					if (result)
//						break;
//				}
//				return false;
//			}
//		case ASTNode.TYPE_VAR_DECL:
//			Type type = (Type) instruction.getChildAt(0).getValue();
//			name = (String) instruction.getChildAt(1).getValue();
//			if (localVariables.peek().containsKey(name))
//				throw new CompilationException("Name " + name
//						+ " already declared", instruction);
//			final Object value;
//			if (instruction.getChildCount() == 3) {
//				value = execute(instruction.getChildAt(2));
//			} else {
//				value = type.getDefaultValue();
//			}
//			localVariables.peek().put(name, new Address(type, value));
//			return null;
//		case ASTNode.TYPE_ASSIGNMENT:
//			ASTNode lvalue = instruction.getChildAt(0);
//			ASTNode rvalue = instruction.getChildAt(1);
//			Object result = execute(rvalue);
//			final Address addr;
//			switch (lvalue.getType()) {
//			case ASTNode.TYPE_ARRAY_ACCESS:
//				name = (String) lvalue.getChildAt(0).getValue();
//				int index = (Integer) execute(lvalue.getChildAt(1));
//				addr = getAddress(classStack.peek(), name, false);
//				((Object[]) addr.getValue())[index] = result;
//				return null;
//			case ASTNode.TYPE_NAME:
//				name = (String) lvalue.getValue();
//				addr = getAddress(classStack.peek(), name, false);
//				addr.setValue(result);
//				return null;
//			}
//		case ASTNode.TYPE_RETURN:
//			returnRegister = execute(instruction.getChildAt(0));
//			returnBreak = true;
//			return null;
//		case ASTNode.TYPE_QUALIFIED_ACCESS:
//			a0 = instruction.getChildAt(0);
//			a1 = instruction.getChildAt(1);
//			objA0 = execute(a0);
//
//			switch (a1.getType()) {
//			case ASTNode.TYPE_VARIABLE:
//				objA1 = a1.getValue();
//				if (objA0 instanceof ClassInstance) {
//					addr = ((ClassInstance) objA0).getValue((String) objA1);
//				} else {
//					addr = ((LoadedClass) objA0).getValue((String) objA1);
//				}
//				instruction.setValueType(addr.getType());
//				return addr.getValue();
//			case ASTNode.TYPE_METHOD_INVOCATION:
//				final Collection<Integer> methodIndex;
//				final LoadedClass clazz;
//				if (objA0 instanceof ClassInstance) {
//					clazz = classPool.get(((ClassInstance) objA0)
//							.getClassDeclaration().getName());
//					methodIndex = clazz.getMethodIndices((String) a1
//							.getChildAt(0).getValue(), false);
//				} else {
//					clazz = (LoadedClass) objA0;
//					methodIndex = clazz.getMethodIndices((String) a1
//							.getChildAt(0).getValue(), true);
//				}
//				List<Object> parameters = new LinkedList<Object>();
//				for (int i = 1; i < a1.getChildCount(); i++) {
//					parameters.add(execute(a1.getChildAt(i)));
//				}
//				for (Integer i : methodIndex) {
//					Variable[] formalParams = clazz.classDef.getMethod(i)
//							.getParameters();
//					for (int j = 0; j < formalParams.length; j++) {
//						if (!a1.getChildAt(j + 1)
//								.getValueType()
//								.isAssignmentCompatible(
//										formalParams[j].getType())) {
//							break;
//						}
//					}
//
//					Method m = clazz.classDef.getMethod(i);
//					
//					instruction.setValueType(m.getReturnType());
//					
//					classStack.push((m.isStatic()) ? null
//							: (ClassInstance) objA0);
//					localVariables.push(new HashMap<String, Address>());
//
//					Iterator<Object> pi = parameters.iterator();
//					for (int k = 0; k < formalParams.length; k++) {
//						localVariables.peek().put(
//								formalParams[k].getName(),
//								new Address(formalParams[k].getType(), pi
//										.next()));
//					}
//
//					for (ASTNode n : clazz.classDef.getImplementation(i).getInstructions()[0].getChildren()) {
//						execute(n);
//						if (returnBreak)
//							break;
//					}
//
//					localVariables.pop();
//					classStack.pop();
//					returnBreak = false;
//					return returnRegister;
//
//					
//				}
//			}
//
//		case ASTNode.TYPE_ARRAY_ACCESS:
//			Object[] array = (Object[]) execute(instruction.getChildAt(0));
//			instruction.setValueType(Type.getElementType(instruction
//					.getChildAt(0).getValueType()));
//			return array[(Integer) execute(instruction.getChildAt(1))];
//		case ASTNode.TYPE_METHOD_INVOCATION:
//			name = (String) instruction.getChildAt(0).getValue();
//			int lastDotIndex = name.lastIndexOf('.');
//			String varName = name.substring(0, lastDotIndex);
//			String methodName = name.substring(lastDotIndex + 1);
//			Address var = getAddress(classStack.peek(), varName, false);
//			ClassInstance inst = (ClassInstance) var.getValue();
//			ClassDefinition<ASTNode> classDef = classPool.get(inst
//					.getClassDeclaration().getName());
//
//			classStack.push(inst);
//
//			for (int i = 0; i < classDef.getMethodCount(); i++) {
//				Method m = classDef.getMethod(i);
//				if (m.equals(methodName)) {
//					Object ret = callMethod(instruction, m,
//							classDef.getImplementation(i));
//					classStack.pop();
//					return ret;
//				}
//			}
//			throw new CompilationException("Unknown method " + methodName,
//					instruction);
//		case ASTNode.TYPE_OP_LAND:
//			instruction.setValueType(Type.BOOLEAN);
//			return (Boolean) execute(instruction.getChildAt(0))
//					&& (Boolean) execute(instruction.getChildAt(1));
//		case ASTNode.TYPE_OP_LOR:
//			instruction.setValueType(Type.BOOLEAN);
//			return (Boolean) execute(instruction.getChildAt(0))
//					|| (Boolean) execute(instruction.getChildAt(1));
//		case ASTNode.TYPE_OP_NEQ:
//			instruction.setValueType(Type.BOOLEAN);
//			return !execute(instruction.getChildAt(0)).equals(
//					execute(instruction.getChildAt(1)));
//		case ASTNode.TYPE_OP_EQU:
//			instruction.setValueType(Type.BOOLEAN);
//			return execute(instruction.getChildAt(0)).equals(
//					execute(instruction.getChildAt(1)));
//		case ASTNode.TYPE_OP_GTH:
//			instruction.setValueType(Type.BOOLEAN);
//			a = (Comparable<Object>) execute(instruction.getChildAt(0));
//			b = (Comparable<Object>) execute(instruction.getChildAt(1));
//			return a.compareTo(b) > 0;
//		case ASTNode.TYPE_OP_LTH:
//			instruction.setValueType(Type.BOOLEAN);
//			a = (Comparable<Object>) execute(instruction.getChildAt(0));
//			b = (Comparable<Object>) execute(instruction.getChildAt(1));
//			return a.compareTo(b) < 0;
//		case ASTNode.TYPE_OP_GEQ:
//			instruction.setValueType(Type.BOOLEAN);
//			a = (Comparable<Object>) execute(instruction.getChildAt(0));
//			b = (Comparable<Object>) execute(instruction.getChildAt(1));
//			return a.compareTo(b) >= 0;
//		case ASTNode.TYPE_OP_LEQ:
//			instruction.setValueType(Type.BOOLEAN);
//			a = (Comparable<Object>) execute(instruction.getChildAt(0));
//			b = (Comparable<Object>) execute(instruction.getChildAt(1));
//			return a.compareTo(b) <= 0;
//		case ASTNode.TYPE_OP_PLUS:
//			performAdd(instruction);
//		case ASTNode.TYPE_OP_MINUS:
//			performSub(instruction);
//		case ASTNode.TYPE_OP_MULT:
//			performMult(instruction);
//		case ASTNode.TYPE_OP_DIV:
//			performDiv(instruction);
//		case ASTNode.TYPE_OP_MOD:
//			performMod(instruction);
//		case ASTNode.TYPE_OP_NEG:
//			performNeg(instruction);
//		case ASTNode.TYPE_OP_LNEG:
//			instruction.setValueType(instruction.getChildAt(0).getValueType());
//			return !(Boolean) execute(instruction.getChildAt(0));
//		case ASTNode.TYPE_LITERAL:
//			Object val = instruction.getValue();
//			instruction.setValueType(Type.getLiteralType(val));
//		case ASTNode.TYPE_VARIABLE:
//			addr = getVariable(classStack.peek(),
//					(String) instruction.getValue());
//			instruction.setValueType(addr.getType());
//			return addr.getValue();
//		default:
//			throw new CompilationException(String.format("Unknown ast type %s",
//					instruction.getType()), instruction);
//		}
//
//	}
//
//	@Override
//	public int execute(ClassDefinition<ASTNode> mainClass, String[] args)
//			throws Exception {
//		for (int i = 0; i < mainClass.getMethodCount(); i++) {
//			Method m = mainClass.getMethod(i);
//			if (m.getName().equals("main")
//					&& m.getParameters().length == 1
//					&& m.getParameters()[0].getType().getName()
//							.equals("String[]")) {
//				classStack.push(null);
//				localVariables.push(new HashMap<String, Address>());
//				localVariables.peek().put(m.getParameters()[0].getName(),
//						new Address(m.getParameters()[0].getType(), args));
//
//				for (ASTNode n : mainClass.getImplementation(i)
//						.getInstructions()) {
//					execute(n);
//				}
//
//				localVariables.pop();
//				classStack.pop();
//
//				returnBreak = false;
//				return (Integer) returnRegister;
//			}
//		}
//		throw new CompilationException("Could not find main method", null);
//	}

}
