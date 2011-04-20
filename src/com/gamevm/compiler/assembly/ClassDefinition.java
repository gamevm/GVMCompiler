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
	
	private static final int DEBUG_INFORMATION = 1;
	private static final int NO_DEBUG_INFORMATION = 0;

//	/**
//	 * The field initialization code describes not the whole code to perform the
//	 * assignment, but rather only the code to evaluate the field initialization
//	 * expression (rvalue).
//	 */
//	protected List<Code<I>> fieldInitializer;
	
	protected Code<I> implicitConstructor;
	protected Code<I> staticConstructor;

	public static ClassDeclaration readHeader(DataInputStream input)
			throws IOException {
		int importCount = input.readInt();
		Type[] imports = new Type[importCount];
		for (int i = 0; i < importCount; i++) {
			imports[i] = Type.importType(input.readUTF());
		}
		
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
				params[j] = new Variable(Type.getType(input.readUTF()),
						input.readUTF());
			}
			methods[i] = new Method(methodModifier, returnType, methodName,
					params);
		}
		int fieldCount = input.readInt();
		Field[] fields = new Field[fieldCount];
		for (int i = 0; i < fieldCount; i++) {
			int fieldModifier = input.readInt();
			Type fieldType = Type.getType(input.readUTF());
			String fieldName = input.readUTF();
			fields[i] = new Field(fieldModifier, fieldType, fieldName);
		}
		return new ClassDeclaration(modifier, name, fields, methods, imports);
	}

	private static void writeHeader(ClassDeclaration d, DataOutputStream output)
			throws IOException {
		output.writeInt(d.imports.length);
		for (Type t : d.imports) {
			output.writeUTF(t.getName());
		}
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
	
	protected static ASTNode readASTNode(DataInputStream input) {
		//TODO:
		return null;
	}
	
	protected static <I extends Instruction> Code<I> readCode(DataInputStream input, CodeReader<I> reader) throws IOException {
		int codeSize = input.readInt();
		if (codeSize > 0) {
			int maxLocals = input.readInt();
			int debuggingFlag = input.readInt();
			List<I> code = new ArrayList<I>(codeSize);
			Map<Instruction, ASTNode> debugInfo = null;
			for (int j = 0; j < codeSize; j++) {
				code.add(reader.readInstruction(input));
			}
			if (debuggingFlag == DEBUG_INFORMATION) {
				debugInfo = new HashMap<Instruction, ASTNode>();
				for (int j = 0; j < codeSize; j++) {
					debugInfo.put(code.get(j), readASTNode(input));
				}
			}
			return new Code<I>(code, debugInfo, maxLocals);
		} else {
			return null;
		}
	}
	
	protected static void writeASTNode(DataOutputStream output, ASTNode n) {
		// TODO:
	}
	
	protected static <I extends Instruction> void writeCode(DataOutputStream output, CodeWriter<I> writer, Code<I> code) throws IOException {
		if (code != null) {
			output.writeInt(code.getSize());
			output.writeInt(code.getMaxLocals());
			output.writeInt(code.hasDebugInformation() ? DEBUG_INFORMATION : NO_DEBUG_INFORMATION);
			for (I instr : code.getInstructions()) {
				writer.writeInstruction(output, instr);
			}
			if (code.hasDebugInformation()) {
				for (I instr : code.getInstructions()) {
					writeASTNode(output, code.getDebugInformation(instr));
				}
			}
		}
	}

	public static <I extends Instruction> ClassDefinition<I> read(
			InputStream stream, CodeReader<I> reader) throws IOException {
		DataInputStream input = new DataInputStream(stream);
		ClassDeclaration d = readHeader(input);
		
		Code<I> staticConstructor = readCode(input, reader);
		Code<I> implicitConstructor = readCode(input, reader);
		
		List<Code<I>> methodImpl = new ArrayList<Code<I>>();
		for (int i = 0; i < d.methods.length; i++) {
			methodImpl.add(readCode(input, reader));
		}
		
		return new ClassDefinition<I>(d, staticConstructor, implicitConstructor, methodImpl);
	}

	public static <I extends Instruction> void write(OutputStream stream,
			CodeWriter<I> writer, ClassDefinition<I> definition)
			throws IOException {
		DataOutputStream output = new DataOutputStream(stream);
		writeHeader(definition.declaration, output);
		
		writeCode(output, writer, definition.staticConstructor);
		writeCode(output, writer, definition.implicitConstructor);
		
		for (Code<I> c : definition.methodImpl) {
			writeCode(output, writer, c);
		}
		
		stream.close();

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
