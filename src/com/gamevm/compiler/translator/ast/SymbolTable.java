package com.gamevm.compiler.translator.ast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.loader.Loader;
import com.gamevm.compiler.assembly.runtime.RuntimeClasses;

public class SymbolTable {

	private Stack<SymbolFrame> symbols;

	private Map<String, ClassSymbol> classSymbols;
	private List<ClassSymbol> classSymbolList;
	private ClassSymbol mainClass;
	private ClassSymbol arrayClass;

	private Loader loader;

	public SymbolTable(ClassDeclaration mainClass, Loader loader) throws IOException {
		symbols = new Stack<SymbolFrame>();
		classSymbols = new HashMap<String, ClassSymbol>();
		classSymbolList = new ArrayList<ClassSymbol>();
		this.loader = loader;
		loadClasses(mainClass);
	}

	public SymbolFrame pushFrame() {
		SymbolFrame current = (symbols.size() > 0) ? symbols.peek() : null;
		int newIndex = (current != null) ? current.getStartIndex() + current.getSize() : 0;
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
			s = new ClassSymbol(ClassSymbol.ARRAY_MASK, RuntimeClasses.DECLARATION_ARRAY);
			classSymbols.put(t.getName(), s);
			arrayClass = s;
		}
		return s;
	}
	
	public ClassSymbol getClass(int index) {
		if (index == ClassSymbol.ARRAY_MASK) {
			return arrayClass;
		} else {
			return classSymbolList.get(index);
		}
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
	
	public Symbol getSymbol(int index) {
		for (int i = symbols.size() - 1; i >= 0; i--) {
			SymbolFrame f = symbols.get(i);
			for (Symbol s : f.getAllSymbols()) {
				if (s.getIndex() == index)
					return s;
			}
		}
		return null;
	}

	public int getIndex(String name) {
		Symbol s = getSymbol(name);
		if (s != null)
			return s.getIndex();
		else
			return -1;
	}

}
