package com.gamevm.execution.ast;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.compiler.assembly.code.ExecutableTreeCode;

public class NativeClass extends LoadedClass {
	
	private Class<? extends ClassInstance> implementation;
	private java.lang.reflect.Method[] methods;
	
	public NativeClass(ClassDeclaration declaration, Class<? extends ClassInstance> implementation) throws SecurityException, NoSuchMethodException {
		super(new ClassDefinition<ExecutableTreeCode>(Environment.FILE_HEADER, declaration, null, null, null), -1);	
		setImplementation(implementation);
	}
	
	private void setImplementation(Class<? extends ClassInstance> implementation) throws SecurityException, NoSuchMethodException {
		this.implementation = implementation;
		final ClassDeclaration decl = getDefinition().getDeclaration();
		
		mapping.put(decl.getType(), implementation);
		
		methods = new java.lang.reflect.Method[decl.getMethods().length];
	
		for (int i = 0; i < methods.length; i++) {
			methods[i] = implementation.getMethod(decl.getMethod(i).getName(), getParameters(decl.getMethod(i)));
		}
	}
	
	
	private Class<?>[] getParameters(Method m) {
		Class<?>[] result = new Class<?>[m.getParameters().length];
		for (int i = 0; i < result.length; i++) {
			result[i] = mapping.get(m.getParameters()[i]);
		}
		return result;
	}
	
	@Override
	public <T> T callNative(int index, ClassInstance thisClass, Object... parameters) {
		try {
			return (T)methods[index].invoke(thisClass, parameters);
		}catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public boolean isNative() {
		return true;
	}

}
