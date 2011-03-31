package com.gamevm.ebc.compiler.grammar;

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
		ejavaLexer lexer = new ejavaLexer(charStream);
		ejavaParser parser = new ejavaParser(new CommonTokenStream(lexer));

		CommonTree tree = parser.program().tree;
		
		ejavaWalker treeWalker = new ejavaWalker(new CommonTreeNodeStream(tree));
		
		return treeWalker.program();
	}

}
