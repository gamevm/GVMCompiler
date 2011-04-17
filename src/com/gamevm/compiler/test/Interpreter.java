package com.gamevm.compiler.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.RecognitionException;

import com.gamevm.compiler.parser.GameCodeParser;
import com.gamevm.compiler.parser.old.CompilationException;
import com.gamevm.compiler.parser.old.Instruction;
import com.gamevm.compiler.parser.old.Statement;
import com.gamevm.compiler.parser.old.Variable;

public class Interpreter {
	
	private static class B { public int Object = 0; }
	
	private static class A { public B lang = new B(); }
	
	/**
	 * @param args
	 * @throws RecognitionException
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws CompilationException 
	 */
	public static void main(String[] args) throws RecognitionException, FileNotFoundException, IOException, CompilationException {
		A java = new A();
		java.lang.Object = 3;
	}

}
