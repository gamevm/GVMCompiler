package com.gamevm.execution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.ClassFileHeader;
import com.gamevm.compiler.assembly.CodeReader;
import com.gamevm.compiler.assembly.GClassLoader;
import com.gamevm.execution.ast.TreeCodeInterpreter;
import com.gamevm.execution.ast.TreeCodeReader;

public class VirtualMachine {
	
	private CodeReader<?>[] codeReaders;
	private Interpreter<?>[] interpreters;
	
	private GClassLoader systemClassLoader;
	
	private RuntimeEnvironment system;
	
	public VirtualMachine() {
		system = new RuntimeEnvironment(System.out, System.err, System.in);
		
		codeReaders = new CodeReader<?>[1];
		codeReaders[ClassFileHeader.CODE_TREE] = new TreeCodeReader();
		interpreters = new Interpreter<?>[1];
		interpreters[ClassFileHeader.CODE_TREE] = new TreeCodeInterpreter(system);

	}
	
	public void run(String mainClassName, File[] classPath, String[] args) throws FileNotFoundException, IOException {
		GClassLoader systemClassLoader = new GClassLoader(classPath);
		ClassFileHeader header = systemClassLoader.readHeader(mainClassName);
		
		CodeReader<?> codeReader = codeReaders[header.getCodeType()];
		Interpreter<?> interpreter = interpreters[header.getCodeType()];
		
		systemClassLoader = new GClassLoader(classPath);
		
		ClassDefinition<?> mainClass = systemClassLoader.readDefinition(mainClassName, codeReader);
		
		interpreter.execute(mainClass, args, null, systemClassLoader);
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		if (args.length == 0)
			System.err.println("Please provide the main class of the game.");
		
		File[] classPath = new File[] {};
		
		int startIndex = 1;
		String[] programArguments = new String[args.length-startIndex];
		System.arraycopy(args, startIndex, programArguments, 0, programArguments.length);
		
		VirtualMachine gvm = new VirtualMachine();
		gvm.run(args[0], classPath, programArguments);
		
		
	}

}
