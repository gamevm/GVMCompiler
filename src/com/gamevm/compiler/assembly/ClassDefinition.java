package com.gamevm.compiler.assembly;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gamevm.compiler.translator.TranslationException;
import com.gamevm.execution.NameTable;

public class ClassDefinition<I extends Instruction> {

	protected ClassDeclaration header;
	protected List<Code<I>> methodImpl;

	/**
	 * The field initialization code describes not the whole code to perform the
	 * assignment, but rather only the code to evaluate the field initialization
	 * expression (rvalue).
	 */
	protected List<Code<I>> fieldInitializer;

	private static ClassDeclaration readHeader(DataInputStream input) throws IOException {
		int modifier = input.readInt();
		String name = input.readUTF();
		int methodCount = input.readInt();
		Method[] methods = new Method[methodCount];
		for (int i = 0; i < methodCount; i++) {
			int methodModifier = input.readInt();
			Type returnType = Type.getType(input.readUTF());
			String methodName = input.readUTF();
			int parameterCount = input.readInt();
			Variable[] params = new Variable[parameterCount];
			for (int j = 0; j < parameterCount; j++) {
				params[j] = new Variable(Type.getType(input.readUTF()), input.readUTF());
			}
			methods[i] = new Method(methodModifier, returnType, methodName, params);
		}
		int fieldCount = input.readInt();
		Field[] fields = new Field[fieldCount];
		for (int i = 0; i < methodCount; i++) {
			int fieldModifier = input.readInt();
			Type fieldType = Type.getType(input.readUTF());
			String fieldName = input.readUTF();
			fields[i] = new Field(fieldModifier, fieldType, fieldName);
		}
		return new ClassDeclaration(modifier, name, fields, methods);
	}

	private static void writeHeader(ClassDeclaration d, DataOutputStream output) throws IOException {
		output.writeInt(d.modifier);
		output.writeUTF(d.name);
		output.writeInt(d.methods.length);
		for (Method m : d.methods) {
			output.writeInt(m.getModifier());
			output.writeUTF(m.getReturnType().getName());
			output.writeUTF(m.getName());
			output.writeInt(m.getParameters().length);
			for (Variable v : m.getParameters()) {
				output.writeUTF(v.getType().getName());
				output.writeUTF(v.getName());
			}
		}
		output.writeInt(d.fields.length);
		for (Field f : d.fields) {
			output.writeInt(f.getModifier());
			output.writeUTF(f.getType().getName());
			output.writeUTF(f.getName());
		}
	}
	
	public static <I extends Instruction> ClassDefinition<I> read(
			InputStream stream, CodeReader<I> reader) throws IOException {
		DataInputStream input = new DataInputStream(stream);
		ClassDeclaration d = readHeader(input);
		List<Code<I>> methodImpl = new ArrayList<Code<I>>();
		List<Code<I>> fieldInitializer = new ArrayList<Code<I>>();
		for (int i = 0; i < d.methods.length; i++) {
			int codeSize = input.readInt();
			Collection<I> code = new ArrayList<I>(codeSize);
			for (int j = 0; j < codeSize; j++) {
				code.add(reader.readInstruction(input));
			}
			methodImpl.add(new Code<I>(code));
		}

		for (int i = 0; i < d.fields.length; i++) {
			int codeSize = input.readInt();
			if (codeSize > 0) {
				Collection<I> code = new ArrayList<I>(codeSize);
				for (int j = 0; j < codeSize; j++) {
					code.add(reader.readInstruction(input));
				}
				fieldInitializer.add(new Code<I>(code));
			} else {
				fieldInitializer.add(null);
			}
		}
		return new ClassDefinition<I>(d, methodImpl, fieldInitializer);
	}

	public static <I extends Instruction> void write(OutputStream stream,
			CodeWriter<I> writer, ClassDefinition<I> definition) throws IOException {
		DataOutputStream output = new DataOutputStream(stream);
		writeHeader(definition.header, output);
		for (Code<I> c : definition.methodImpl) {
			output.writeInt(c.getSize());
			for (I instr : c.getInstructions()) {
				writer.writeInstruction(output, instr);
			}
		}
		for (Code<I> c : definition.fieldInitializer) {
			output.writeInt(c.getSize());
			for (I instr : c.getInstructions()) {
				writer.writeInstruction(output, instr);
			}
		}
	}

	public ClassDefinition(ClassDeclaration header, List<Code<I>> methodImpl,
			List<Code<I>> fieldInitializer) {
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
			methodImpl.add(translator.translate(header.methods[i],
					cdef.methodImpl.get(i)));
		}

		for (int i = 0; i < header.fields.length; i++) {
			if (cdef.fieldInitializer.get(i) != null)
				fieldInitializer.add(translator.translate(
						new Method(0, header.fields[i].getType(), String
								.format("<init-%s>", header.fields[i].name)),
						cdef.fieldInitializer.get(i)));
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
		return getImplementation(header.getMethod(Modifier.PUBLIC, true,
				"main", Type.getType("gc.String[]")));
	}

	public String toString(NameTable names) {
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
				b.append(initCode.toString(4, names));
			}
			b.append('\n');
		}

		for (int i = 0; i < methods.length; i++) {
			b.append("  ");
			b.append(methods[i]);
			Code<I> code = methodImpl.get(i);
			if (code != null) {
				b.append('\n');
				b.append(code.toString(4, names));
			}
			b.append('\n');
		}

		return b.toString();
	}

}
