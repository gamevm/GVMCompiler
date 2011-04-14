package com.gamevm.compiler.translator.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.Field;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.compiler.assembly.Type;
import com.gamevm.execution.NameTable;

public class SymbolTable implements NameTable {
	
	private Stack<SymbolFrame> symbols;
	
	private Map<String, ClassSymbol> classSymbols;
	private List<ClassSymbol> classSymbolList;
	private ClassSymbol mainClass;
	
	public SymbolTable(ClassDeclaration mainClass) {
		symbols = new Stack<SymbolFrame>();
		this.mainClass = new ClassSymbol(0, mainClass);
		classSymbols = new HashMap<String, ClassSymbol>();
		classSymbolList = new ArrayList<ClassSymbol>();
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
	
	protected void loadClass(ClassDeclaration c) {
		ClassSymbol s = new ClassSymbol(classSymbolList.size(), c);
		loadClass(s);
	}
	
	protected void loadClass(ClassSymbol s) {
		classSymbols.put(s.getName(), s);
		classSymbolList.add(s);
	}
	
	protected void loadClasses() {
		loadClass(mainClass);
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

	@Override
	public String getLocalVariableName(int index) {
		for (SymbolFrame frame : symbols) {
			if (index < frame.getStartIndex() + frame.getSize()) {
				return frame.getSymbol(index).getName();
			}
		}
		throw new IllegalArgumentException("Invalid local variable index " + index);
	}

	@Override
	public Method getMethod(int classIndex, int methodIndex) {
		return classSymbolList.get(classIndex).getDeclaration().getMethod(methodIndex);
	}

	@Override
	public String getClassName(int classIndex) {
		return classSymbolList.get(classIndex).getName();
	}

	@Override
	public Field getField(int classIndex, int fieldIndex) {
		return classSymbolList.get(classIndex).getDeclaration().getField(fieldIndex);
	}

}
