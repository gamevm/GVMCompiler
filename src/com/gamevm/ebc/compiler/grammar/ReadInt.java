package com.gamevm.ebc.compiler.grammar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;

public class ReadInt implements Expression {
	
	private BufferedReader input;

	public ReadInt() {
		input = new BufferedReader(new InputStreamReader(System.in));
	}

	@Override
	public Object evaluate(Map<String, Object> environment) {
		try {
			return Long.parseLong(input.readLine());
		} catch (NumberFormatException e) {
			return 0;
		} catch (IOException e) {
			return 0;
		}
	}

	@Override
	public Class<?> inferType() throws CompilationException {
		return Integer.class;
	}

	@Override
	public Collection<Instruction> compile() throws CompilationException {
		throw new CompilationException("Statement not supported");
	}

}
