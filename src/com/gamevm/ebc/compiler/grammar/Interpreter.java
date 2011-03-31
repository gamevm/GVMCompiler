package com.gamevm.ebc.compiler.grammar;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.RecognitionException;

public class Interpreter {
	
	/**
	 * @param args
	 * @throws RecognitionException
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws CompilationException 
	 */
	public static void main(String[] args) throws RecognitionException, FileNotFoundException, IOException, CompilationException {
		if (args.length == 0)
			System.err.println("You must provide at least a script");
		
		List<Statement> program = EJParser.parse(new FileReader(args[0]));
		LinkedList<Instruction> instr = new LinkedList<Instruction>();
		
		for (int i = 1; i < args.length; i++) {
			String name = "$" + (i-1);
			
			try {
				long l = Long.parseLong(args[i]);
				Variable.declareVariable(Long.class, name, l);
			} catch (NumberFormatException e) {
				Variable.declareVariable(String.class, name, args[i]);
			}
			
		}
		
		for (Statement s : program) {
			instr.addAll(s.compile());
		}
		
		Instruction.execute(instr.toArray(new Instruction[] {}));
	}

}
