package com.gamevm.compiler.parser.old;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.RecognitionException;

public class Compiler {

	public static void main(String[] args) throws FileNotFoundException, IOException, RecognitionException, CompilationException {
		if (args.length < 2)
			System.err.println("You must provide at least a script");
		
//		List<Statement> program = GameCodeParser.parse(new FileReader(args[0]));
//		
//		DataOutputStream output = new DataOutputStream(new FileOutputStream(args[1]));
//		
//		Collection<Instruction> instr = new LinkedList<Instruction>();
//		
//		for (Statement s : program) {
//			instr.addAll(s.compile());
//		}
//		
//		for (Instruction i : instr) {
//			i.write(output);
//		}
//		
//		output.close();
	}
	
}
