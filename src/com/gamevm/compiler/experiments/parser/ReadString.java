package com.gamevm.compiler.experiments.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;

public class ReadString implements Expression {
	
	private BufferedReader input;

	public ReadString() {
		input = new BufferedReader(new InputStreamReader(System.in));
	}

	@Override
	public Object evaluate(Map<String, Object> environment) {
		try {
			return input.readLine();
		} catch (NumberFormatException e) {
			return "";
		} catch (IOException e) {
			return "";
		}
	}

	@Override
	public Class<?> inferType() throws CompilationException {
		return String.class;
	}

	@Override
	public Collection<Instruction> compile() throws CompilationException {
		throw new CompilationException("Statement not supported");
	}

}
