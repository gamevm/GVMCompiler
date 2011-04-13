package com.gamevm.compiler.assembly;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;

import com.gamevm.compiler.translator.TranslationException;
import com.gamevm.utils.StringFormatter;

public class ClassDefinition<I extends Instruction> {

	protected ClassDeclaration header;
	protected List<Code<I>> methodImpl;

	/**
	 * The field initialization code describes not the whole code to perform the
	 * assignment, but rather only the code to evaluate the field initialization
	 * expression (rvalue).
	 */
	protected List<Code<I>> fieldInitializer;
	
	public ClassDefinition(ClassDeclaration header, List<Code<I>> methodImpl, List<Code<I>> fieldInitializer) {
		this.header = header;
		this.methodImpl = methodImpl;
		this.fieldInitializer = fieldInitializer;
	}

	public <S extends Instruction> ClassDefinition(ClassDefinition<S> cdef,
			Translator<S, I> translator) throws TranslationException {
		this.header = cdef.header;
		methodImpl = new ArrayList<Code<I>>(cdef.methodImpl.size());
		fieldInitializer = new ArrayList<Code<I>>(cdef.fieldInitializer.size());

		for (int i = 0; i < header.methods.length; i++) {
			methodImpl.add(translator.translate(header.methods[i], cdef.methodImpl.get(i)));
		}

		for (int i = 0; i < header.fields.length; i++) {
			if (cdef.fieldInitializer.get(i) != null)
				fieldInitializer.add(translator.translate(new Method(0, header.fields[i].getType(), String.format("<init-%s>", header.fields[i].name)), cdef.fieldInitializer.get(i)));
			else
				fieldInitializer.add(null);
		}

	}

	public Code<I> getImplementation(int method) {
		return methodImpl.get(method);
	}
	
	public Code<I> getFieldInitialization(int field) {
		return fieldInitializer.get(field);
	}

	public Method getMethod(int i) {
		return header.methods[i];
	}
	
	public int getMethodCount() {
		return header.methods.length;
	}

	public Field getField(int i) {
		return header.fields[i];
	}
	
	public int getFieldCount() {
		return header.fields.length;
	}
	
	public ClassDeclaration getDeclaration() {
		return header;
	}
	
	public Code<I> getMain() {
		return getImplementation(header.getMethod("main", Type.getType("gc.String[]")));
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		
		b.append(Modifier.toString(header.modifier));
		
		b.append("class ");
		b.append(header.name);
		
		// TODO: extensions
		
		b.append("\n\n");
		final Field[] fields = header.fields;
		final Method[] methods = header.methods;
		for (int i = 0; i < fields.length; i++) {
			b.append("  ");
			b.append(fields[i]);
			Code<I> initCode = fieldInitializer.get(i);
			if (initCode != null) {
				b.append('\n');
				b.append(initCode.toString(4));
			}
			b.append('\n');
		}
		
		for (int i = 0; i < methods.length; i++) {
			b.append("  ");
			b.append(methods[i]);
			Code<I> code = methodImpl.get(i);
			if (code != null) {
				b.append('\n');
				b.append(code.toString(4));
			}
			b.append('\n');
		}
		
		return b.toString();
	}

}
