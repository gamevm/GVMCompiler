package com.gamevm.compiler.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.CompilationException;
import com.gamevm.compiler.parser.ASTNode;
import com.gamevm.compiler.parser.GCASTLexer;
import com.gamevm.compiler.parser.GCASTParser;
import com.gamevm.compiler.parser.ParserError;

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
	public static void main(String[] args) throws RecognitionException, FileNotFoundException, IOException {
		CharStream charStream = new ANTLRStringStream("");
		GCASTLexer lexer = new GCASTLexer(charStream);
		GCASTParser parser = new GCASTParser(new CommonTokenStream(lexer));
		ClassDefinition<ASTNode> ast = parser.program();
		
		List<ParserError> errors = parser.getErrors();
		for (ParserError e : errors) {
			System.out.println(e.getMessage(parser));
		}
		
	}

}
