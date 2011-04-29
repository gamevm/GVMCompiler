package com.gamevm.compiler.translator.ast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.Field;
import com.gamevm.compiler.assembly.GClassLoader;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.compiler.assembly.Modifier;

public class SymbolTable {

	private Stack<SymbolFrame> symbols;

	private Map<String, ClassSymbol> classSymbols;
	private List<ClassSymbol> classSymbolList;
	private ClassSymbol mainClass;

	private GClassLoader loader;

	public static final ClassDeclaration ARRAY_DECLARATION = new ClassDeclaration(Modifier.getFlag(Modifier.PUBLIC,
			false, true), "gc.Array", new Field[] { new Field(Modifier.getFlag(Modifier.PUBLIC, false, true), Type.INT,
			"length") }, new Method[0], new Type[0]);

	public SymbolTable(ClassDeclaration mainClass, GClassLoader loader) throws IOException {
		symbols = new Stack<SymbolFrame>();
		classSymbols = new HashMap<String, ClassSymbol>();
		classSymbolList = new ArrayList<ClassSymbol>();
		this.loader = loader;
		loadClasses(mainClass);
	}

	public SymbolFrame pushFrame(boolean isStackFrame) {
		SymbolFrame current = (symbols.size() > 0) ? symbols.peek() : null;
		int newIndex = (!isStackFrame && current != null) ? current.getStartIndex() + current.getSize() : 0;
		return symbols.push(new SymbolFrame(newIndex));
	}

	public void popFrame() {
		symbols.pop();
	}

	protected ClassSymbol loadClass(ClassDeclaration c) throws IOException {
		ClassSymbol s = new ClassSymbol(classSymbolList.size(), c);
		classSymbols.put(s.getName(), s);
		classSymbolList.add(s);
		for (Type t : c.getImports()) {
			loadClass(t);
		}
		return s;
	}

	protected void loadClasses(ClassDeclaration mainClass) throws IOException {
		Type mainType = Type.getType(mainClass.getName());
		for (Type t : Type.IMPLICIT_IMPORTS) {
			if (t != mainType)
				loadClass(t);
		}
		this.mainClass = loadClass(mainClass);
	}

	protected void loadClass(Type type) throws IOException {
		if (!classSymbols.containsKey(type.getName())) {
			ClassDeclaration d = loader.readDeclaration(type.getName());
			loadClass(d);
		}
	}

	public ClassSymbol getMainClass() {
		return mainClass;
	}

	public ClassSymbol getClass(Type t) {
		ClassSymbol s = classSymbols.get(t.getName());
		if (s == null && t.isArrayType()) {
			s = new ClassSymbol(ClassSymbol.ARRAY_MASK, ARRAY_DECLARATION);
			classSymbols.put(t.getName(), s);
		}
		return s;
	}

	public int add(String name, Type type) {
		return symbols.peek().addSymbol(name, type);
	}

	public Symbol getSymbol(String name) {
		Symbol result = null;
		for (int i = symbols.size() - 1; i >= 0; i--) {
			result = symbols.get(i).getSymbol(name);
			if (result != null)
				return result;
		}
		return result;
	}

	public int getIndex(String name) {
		Symbol s = getSymbol(name);
		if (s != null)
			return s.getIndex();
		else
			return -1;
	}

}
