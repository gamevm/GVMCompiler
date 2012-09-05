package com.gamevm.compiler.assembly;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.assembly.code.ASTCodeFactory;
import com.gamevm.compiler.assembly.code.Code;
import com.gamevm.compiler.assembly.code.CodeFactory;
import com.gamevm.compiler.assembly.code.ExecutableTreeCodeFactory;
import com.gamevm.compiler.translator.TranslationException;
import com.gamevm.compiler.translator.Translator;
import com.gamevm.utils.StringFormatter;

public class ClassDefinition<C extends Code> {

	public static final Method IMPLICIT_CONSTRUCTOR = new Method(Modifier.PRIVATE, Type.VOID, "<implicit>");
	public static final Method STATIC_CONSTRUCTOR = new Method(Modifier.getFlag(Modifier.PRIVATE, true, true),
			Type.VOID, "<static>");

	public static final ClassFileHeader AST_HEADER = new ClassFileHeader(1, Code.AST_TREE);

	private static CodeFactory<?>[] codeFactories = new CodeFactory<?>[Code.MAX_CODE_TYPE];

	static {
		codeFactories[Code.AST_TREE] = new ASTCodeFactory();
		codeFactories[Code.CODE_TREE] = new ExecutableTreeCodeFactory();
		codeFactories[Code.DECLARATION_ONLY] = null;
	}

	protected ClassFileHeader header;
	protected ClassDeclaration declaration;
	protected List<C> methodImpl;

	// /**
	// * The field initialization code describes not the whole code to perform
	// the
	// * assignment, but rather only the code to evaluate the field
	// initialization
	// * expression (rvalue).
	// */
	// protected List<Code<I>> fieldInitializer;

	protected C implicitConstructor;
	protected C staticConstructor;

	protected void writeCode(ObjectOutputStream output, C code) throws IOException {
		if (header.hasDefinition()) {
			if (code != null) {
				code.write(output);
			} else {
				output.writeInt(0);
			}
		}
	}
	
	protected C readCode(ObjectInputStream input, CodeFactory<C> f) throws IOException {
		C code = f.newCode();
		code.read(input);
		return code;
	}

	public ClassDefinition(ClassFileHeader header, ClassDeclaration declaration, C staticConstructor,
			C implicitConstructor, List<C> methodImpl) {
		this.header = header;
		this.declaration = declaration;
		this.methodImpl = methodImpl;
		this.staticConstructor = staticConstructor;
		this.implicitConstructor = implicitConstructor;
	}

	public <S extends Code> ClassDefinition(ClassDefinition<S> cdef, Translator<S, C> translator, CodeFactory<C> codeFactory)
			throws TranslationException {
		this.declaration = cdef.declaration;
		this.header = new ClassFileHeader(cdef.header.getVersion(), codeFactory.getCodeIdentifier());
		methodImpl = new ArrayList<C>(cdef.methodImpl.size());

		for (int i = 0; i < declaration.methods.length; i++) {
			methodImpl.add(translator.translate(declaration.methods[i], cdef.methodImpl.get(i)));
		}

		staticConstructor = translator.translate(STATIC_CONSTRUCTOR, cdef.staticConstructor);
		implicitConstructor = translator.translate(IMPLICIT_CONSTRUCTOR, cdef.implicitConstructor);
	}

	@SuppressWarnings("unchecked")
	public ClassDefinition(InputStream stream) throws IOException {
		ObjectInputStream input = new ObjectInputStream(stream);
		header = new ClassFileHeader(input);

		declaration = new ClassDeclaration(input);

		CodeFactory<C> codeFactory = (CodeFactory<C>)codeFactories[header.getCodeType()];
		staticConstructor = readCode(input, codeFactory);
		implicitConstructor = readCode(input, codeFactory);

		methodImpl = new ArrayList<C>();
		for (int i = 0; i < declaration.methods.length; i++) {
			methodImpl.add(readCode(input, codeFactory));
		}
		input.close();
	}

	public void write(OutputStream stream) throws IOException {
		ObjectOutputStream output = new ObjectOutputStream(stream);

		header.write(output);
		declaration.write(output);

		staticConstructor.write(output);
		implicitConstructor.write(output);

		for (Code c : methodImpl) {
			c.write(output);
		}

		stream.close();
	}

	public C getImplementation(int method) {
		return methodImpl.get(method);
	}

	public C getStaticConstructor() {
		return staticConstructor;
	}

	public C getImplicitConstructor() {
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

	public C getMain() {
		return getImplementation(declaration.getMethod(Modifier.PUBLIC, true, "main", Type.getType("gc.String[]")));
	}

	public String toDebugString() {
		StringBuilder b = new StringBuilder();

		b.append(Modifier.toString(declaration.modifier));

		b.append("class ");
		b.append(declaration.name);
		if (declaration.parentClass != null) {
			b.append(" extends ");
			b.append(declaration.parentClass);
		}
		if (declaration.parentInterfaces.length > 0)
			b.append(" implements ");
		b.append(StringFormatter.printIterable(declaration.parentInterfaces, ", "));

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
		if (staticConstructor != null)
			b.append(staticConstructor.toString(4));
		b.append("\n");

		b.append("  <implicit>\n");
		if (implicitConstructor != null)
			b.append(implicitConstructor.toString(4));
		b.append("\n");

		for (int i = 0; i < methods.length; i++) {
			b.append("  ");
			b.append(methods[i]);
			C code = methodImpl.get(i);
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
