package com.gamevm.compiler.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.Instruction;

public class BinaryViewer {
	
	private static <I extends Instruction> ClassDefinition<I> readClass(InputStream contentStream) throws IOException {
		return new ClassDefinition<I>(contentStream);
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		if (args.length != 1) {
			System.out.println("Please provide exactly one class name.");
			return;
		}
		
		File classPath = new File("code/bin");
		
		File classFile = new File(classPath, args[0].replace('.', '/') + ".gbc");
		
		ClassDefinition<?> classDefinition = readClass(new FileInputStream(classFile));
		System.out.println(classDefinition.toDebugString());
	}

}
