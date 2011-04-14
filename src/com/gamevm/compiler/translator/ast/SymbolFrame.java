package com.gamevm.compiler.translator.ast;

import java.util.HashMap;
import java.util.Map;

import com.gamevm.compiler.assembly.Type;

public class SymbolFrame {
	
	private int startIndex;
	private Map<String, Symbol> symbols;
	
	public SymbolFrame(int startIndex) {
		this.startIndex = startIndex;
		symbols = new HashMap<String, Symbol>();
	}
	
	public int addSymbol(String name, Type type) {
		int index = symbols.size();
		Symbol old = symbols.put(name, new Symbol(name, index, type));
		if (old != null)
			throw new IllegalArgumentException(String.format("Multiple definitions of symbol %s", name));
		return index;
	}
	
	public Symbol getSymbol(int index) {
		int relIndex = index - startIndex;
		for (Symbol s : symbols.values()) {
			if (s.getIndex() == relIndex)
				return s;
		}
		return null;
	}
	
	public Symbol getSymbol(String name) {
		return symbols.get(name);
	}
	
	public int getStartIndex() {
		return startIndex;
	}
	
	public int getSize() {
		return symbols.size();
	}

}
