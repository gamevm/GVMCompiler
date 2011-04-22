package com.gamevm.compiler.assembly;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gamevm.compiler.parser.ASTNode;
import com.gamevm.compiler.translator.Code;
import com.gamevm.compiler.translator.TranslationException;
import com.gamevm.compiler.translator.Translator;

public class ClassDefinition<I extends Instruction> {
	
	public static final Method IMPLICIT_CONSTRUCTOR = new Method(Modifier.PRIVATE, Type.VOID, "<implicit>");
	public static final Method STATIC_CONSTRUCTOR = new Method(Modifier.getFlag(Modifier.PRIVATE, true, true), Type.VOID, "<static>");

	protected ClassFileHeader header;
	protected ClassDeclaration declaration;
	protected List<Code<I>> methodImpl;
	
	

//	/**
//	 * The field initialization code describes not the whole code to perform the
//	 * assignment, but rather only the code to evaluate the field initialization
//	 * expression (rvalue).
//	 */
//	protected List<Code<I>> fieldInitializer;
	
	protected Code<I> implicitConstructor;
	protected Code<I> staticConstructor;
	
	protected static <I extends Instruction> Code<I> readCode(DataInputStream input, CodeReader<I> reader) throws IOException {
		int codeSize = input.readInt();
		if (codeSize > 0) {
			return new Code<I>(input, reader, codeSize);
		} else {
			return null;
		}
	}
	
	protected static <I extends Instruction> void writeCode(DataOutputStream output, CodeWriter<I> writer, Code<I> code) throws IOException {
		if (code != null) {
			code.write(output, writer);
		} else {
			output.writeInt(0);
		}
	}

	public ClassDefinition(ClassDeclaration header, Code<I> staticConstructor, Code<I> implicitConstructor, List<Code<I>> methodImpl) {
		this.declaration = header;
		this.methodImpl = methodImpl;
		this.staticConstructor = staticConstructor;
		this.implicitConstructor = implicitConstructor;
	}

	public <S extends Instruction> ClassDefinition(ClassDefinition<S> cdef,
			Translator<S, I> translator) throws TranslationException {
		this.declaration = cdef.declaration;
		methodImpl = new ArrayList<Code<I>>(cdef.methodImpl.size());

		for (int i = 0; i < declaration.methods.length; i++) {
			methodImpl.add(translator.translate(declaration.methods[i],
					cdef.methodImpl.get(i)));
		}
		
		staticConstructor = translator.translate(STATIC_CONSTRUCTOR, cdef.staticConstructor);
		implicitConstructor = translator.translate(IMPLICIT_CONSTRUCTOR, cdef.implicitConstructor);
	}
	
	public ClassDefinition(InputStream stream, CodeReader<I> reader) throws IOException {
		DataInputStream input = new DataInputStream(stream);
		header = new ClassFileHeader(input);
		declaration = new ClassDeclaration(input);
		
		staticConstructor = readCode(input, reader);
		implicitConstructor = readCode(input, reader);
		
		methodImpl = new ArrayList<Code<I>>();
		for (int i = 0; i < declaration.methods.length; i++) {
			methodImpl.add(readCode(input, reader));
		}
		input.close();
	}
	
	public void write(OutputStream stream, CodeWriter<I> writer) throws IOException {
		DataOutputStream output = new DataOutputStream(stream);
		
		header.write(output);
		declaration.write(output);
		
		writeCode(output, writer, staticConstructor);
		writeCode(output, writer, implicitConstructor);
		
		for (Code<I> c : methodImpl) {
			writeCode(output, writer, c);
		}
		
		stream.close();
	}

	public Code<I> getImplementation(int method) {
		return methodImpl.get(method);
	}

	public Code<I> getStaticConstructor() {
		return staticConstructor;
	}
	
	public Code<I> getImplicitConstructor() {
		return implicitConstructor;
	}

	public Method getMethod(int i) {
		return declaration.methods[i];
	}

	public int getMethodCount() {
		return declaration.methods.length;
	}

	public Field getField(int i) {
		return declaration.fields[i];
	}

	public int getFieldCount() {
		return declaration.fields.length;
	}

	public ClassDeclaration getDeclaration() {
		return declaration;
	}

	public Code<I> getMain() {
		return getImplementation(declaration.getMethod(Modifier.PUBLIC, true,
				"main", Type.getType("gc.String[]")));
	}

	public String toDebugString() {
		StringBuilder b = new StringBuilder();

		b.append(Modifier.toString(declaration.modifier));

		b.append("class ");
		b.append(declaration.name);

		// TODO: extensions

		b.append("\n\n");
		final Field[] fields = declaration.fields;
		final Method[] methods = declaration.methods;
		for (int i = 0; i < fields.length; i++) {
			b.append("  ");
			b.append(fields[i]);
			b.append('\n');
		}
		b.append("\n");
		
		b.append("  <static>\n");
		b.append(staticConstructor.toString(4));
		b.append("\n");
		
		b.append("  <implicit>\n");
		b.append(implicitConstructor.toString(4));
		b.append("\n");

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
	
	@Override
	public String toString() {
		return declaration.name;
	}

}
