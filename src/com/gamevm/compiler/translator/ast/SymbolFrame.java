package com.gamevm.compiler.translator.ast;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.gamevm.compiler.Type;

public class SymbolFrame {
	
	private int startIndex;
	//private boolean isStackFrame;
	private Map<String, Symbol> symbols;
	
	public SymbolFrame(int startIndex) {
		this.startIndex = startIndex;
		//this.isStackFrame = isStackFrame;
		symbols = new HashMap<String, Symbol>();
	}
	
	public int addSymbol(String name, Type type) {
		int index = symbols.size() + startIndex;
		Symbol newSymbol = new Symbol(name, index, type);
		Symbol old = symbols.put(name, newSymbol);
		if (old != null)
			throw new IllegalArgumentException(String.format("Multiple definitions of symbol %s", name));
		return index;
	}
	
//	public Symbol getSymbol(int index) {
//		for (Symbol s : symbols.values()) {
//			if (s.getIndex() == index)
//				return s;
//		}
//		return null;
//	}
	public Collection<Symbol> getAllSymbols() {
		return symbols.values();
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
