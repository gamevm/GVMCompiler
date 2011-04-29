package com.gamevm.compiler.assembly.runtime;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassFileHeader;
import com.gamevm.compiler.assembly.Field;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.compiler.assembly.Modifier;
import com.gamevm.compiler.assembly.Variable;

public class RuntimeClasses {
	
	private static final int PUBLIC_FINAL = Modifier.getFlag(Modifier.PUBLIC, false, true);
	private static final int PUBLIC_STATIC_FINAL = Modifier.getFlag(Modifier.PUBLIC, true, true);
	
	private static final Field[] NO_FIELDS = new Field[0];
	private static final Type[] NO_IMPORTS = new Type[0];
	
	private static final Map<String, ClassDeclaration> declarations = new HashMap<String, ClassDeclaration>();
	
	public static final ClassDeclaration DECLARATION_STRING = new ClassDeclaration(PUBLIC_FINAL, "gc.String", NO_FIELDS,
			new Method[] {
			new Method(PUBLIC_FINAL, Type.INT, "length"),
			new Method(PUBLIC_FINAL, Type.getArrayType(Type.CHAR, 1), "toCharArray")
	}, NO_IMPORTS);
	
	
	public static final int METHOD_STRING_LENGTH = 0;
	public static final int METHOD_STRING_TOCHARARRAY = 1;
	
	public static final ClassDeclaration DECLARATION_SYSTEM = new ClassDeclaration(PUBLIC_FINAL, "gc.System", NO_FIELDS,
			new Method[] {
			new Method(PUBLIC_STATIC_FINAL, Type.VOID, "print",
					new Variable(Type.getType("gc.String"), "arg")),
			new Method(PUBLIC_STATIC_FINAL, Type.INT, "getCharacterValue",
					new Variable(Type.CHAR, "arg"))
	}, NO_IMPORTS);
	
	public static final int METHOD_SYSTEM_PRINT = 0;
	public static final int METHOD_SYSTEM_GET_CHARACTER_VALUE = 1;
	
	static {
		declarations.put(DECLARATION_STRING.getName(), DECLARATION_STRING);
		declarations.put(DECLARATION_SYSTEM.getName(), DECLARATION_SYSTEM);
	}
	
	public static final ClassFileHeader DECLARATION_HEADER = new ClassFileHeader(1, ClassFileHeader.DECLARATION_ONLY);
	
	public static void generateRuntimeLibrary(File targetDirectory) throws IOException {
		for (ClassDeclaration d : declarations.values()) {
			
			String relPath = d.getName().replace('.', '/') + ".gbc";
			File classFile = new File(targetDirectory, relPath);
			classFile.getParentFile().mkdirs();
			if (!classFile.exists())
				classFile.createNewFile();
			
			DataOutputStream output = new DataOutputStream(new FileOutputStream(classFile));
			DECLARATION_HEADER.write(output);
			d.write(output);
			output.close();
		}
	}

}
