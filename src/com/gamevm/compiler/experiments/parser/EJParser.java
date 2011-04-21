package com.gamevm.compiler.experiments.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

public class EJParser {
	
	public static List<Statement> parse(Reader input) throws IOException, RecognitionException {
		CharStream charStream = new ANTLRReaderStream(input);
		GCLexer lexer = new GCLexer(charStream);
		GCParser parser = new GCParser(new CommonTokenStream(lexer));

		CommonTree tree = (CommonTree)parser.program().getTree();
		
		GCWalker treeWalker = new GCWalker(new CommonTreeNodeStream(tree));
		
		return treeWalker.program();
	}

}
