package com.gamevm.compiler.parser;

import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;

public class ParserError {
	
	private RecognitionException exception;
	private String[] tokens;
	
	public ParserError(RecognitionException exception, String[] tokens) {
		super();
		this.exception = exception;
		this.tokens = tokens;
	}
	
	public String getMessage(Parser parser) {
		return parser.getErrorHeader(exception) + " " + parser.getErrorMessage(exception, tokens);
	}
	
	public int getLine() {
		return exception.line;
	}

}
