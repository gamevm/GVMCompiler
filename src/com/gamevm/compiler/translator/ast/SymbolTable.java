package com.gamevm.compiler.translator.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.Type;

public class SymbolTable {
	
	private Stack<SymbolFrame> symbols;
	
	private Map<String, ClassSymbol> classSymbols;
	private ClassSymbol mainClass;
	
	public SymbolTable(ClassDeclaration mainClass) {
		symbols = new Stack<SymbolFrame>();
		this.mainClass = new ClassSymbol(0, mainClass);
		classSymbols = new HashMap<String, ClassSymbol>();
		loadClasses();
	}
	
	public SymbolFrame pushFrame() {
		SymbolFrame current = (symbols.size() > 0) ? symbols.peek() : null;
		int newIndex = (current != null) ? current.getStartIndex() + current.getSize() : 0;
		return symbols.push(new SymbolFrame(newIndex));
	}
	
	public void popFrame() {
		symbols.pop();
	}
	
	protected void loadClasses() {
		int counter = 1;
		classSymbols.put(mainClass.getName(), mainClass);
	}
	
	public ClassSymbol getMainClass() {
		return mainClass;
	}
	
	public ClassSymbol getClass(String name) {
		return classSymbols.get(name);
	}
	
	public int add(String name, Type type) {
		return symbols.peek().addSymbol(name, type);
	}
	
	public Symbol getSymbol(String name) {
		Symbol result = null;
		for (int i = symbols.size()-1; i >= 0; i--) {
			result = symbols.get(i).getSymbol(name);
			if (result != null)
				return result;
		}
		return result;
	}
	
	public int getIndex(String name) {
		for (int i = symbols.size()-1; i >= 0; i--) {
			Symbol s = symbols.get(i).getSymbol(name);
			if (s != null)
				return symbols.get(i).getStartIndex() + s.getIndex();
		}
		return -1;
	}

}
