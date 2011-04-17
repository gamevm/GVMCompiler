package com.gamevm.compiler.parser;

import java.io.IOException;
import java.io.Reader;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;

public class GameCodeParser {
	
	public static CommonTree parse(Reader input) throws IOException, RecognitionException {
		CharStream charStream = new ANTLRReaderStream(input);
		GCLexer lexer = new GCLexer(charStream);
		GCParser parser = new GCParser(new CommonTokenStream(lexer));

		return (CommonTree)parser.program().getTree();
	}

}
