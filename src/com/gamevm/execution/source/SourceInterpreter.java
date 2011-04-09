package com.gamevm.execution.source;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import com.gamevm.compiler.parser.GameCodeParser;
import com.gamevm.execution.Interpreter;

public class SourceInterpreter extends Interpreter {

	public SourceInterpreter(PrintStream out, PrintStream err, InputStream in) {
		super(out, err, in);
	}

	@Override
	public void execute(Reader input, String[] args) throws IOException {
		try {
			CommonTree tree = GameCodeParser.parse(input);
			SourceInterpreterWalker walker = new SourceInterpreterWalker(new CommonTreeNodeStream(tree));
			
		}catch (RecognitionException e) {
			e.printStackTrace(out);
		}
	}
	
	

}
