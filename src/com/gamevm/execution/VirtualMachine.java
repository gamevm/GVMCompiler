package com.gamevm.execution;

import java.io.File;

import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.ClassFileHeader;
import com.gamevm.compiler.assembly.GClassLoader;
import com.gamevm.compiler.assembly.Instruction;
import com.gamevm.compiler.assembly.code.Code;
import com.gamevm.execution.ast.TreeCodeInterpreter;
import com.gamevm.utils.commandline.Arguments;

public class VirtualMachine {

	// private CodeReader<?>[] codeReaders;
	private Interpreter<?>[] interpreters;

	private GClassLoader systemClassLoader;

	private RuntimeEnvironment system;

	public VirtualMachine() {
		system = new RuntimeEnvironment(System.out, System.err, System.in);

		// codeReaders = new CodeReader<?>[ClassFileHeader.MAX_CODE_TYPE];
		// codeReaders[ClassFileHeader.CODE_TREE] = new TreeCodeReader();
		interpreters = new Interpreter<?>[Code.MAX_CODE_TYPE];
		interpreters[Code.CODE_TREE] = new TreeCodeInterpreter(
				system);

	}

	@SuppressWarnings("unchecked")
	public <C extends Code> void run(String mainClassName,
			File[] classPath, String[] args) throws Exception {
		GClassLoader systemClassLoader = new GClassLoader(classPath);
		ClassFileHeader header = systemClassLoader.readHeader(mainClassName);
		Interpreter<C> interpreter = (Interpreter<C>) interpreters[header
				.getCodeType()];

		systemClassLoader = new GClassLoader(classPath);
		ClassDefinition<C> mainClass = systemClassLoader
				.readDefinition(mainClassName);
		interpreter.execute(mainClass, args, null, systemClassLoader);
	}

	private static File[] parseClassPath(String s) {
		final File[] cp;
		if (s != null) {
			String[] paths = s.split(":");
			cp = new File[paths.length + 1];
			for (int i = 0; i < paths.length; i++) {
				cp[i] = new File(paths[i]);
			}
		} else {
			cp = new File[1];
		}
		cp[cp.length - 1] = new File(".");
		return cp;
	}

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.err.println("Please provide the main class of the game.");
			System.exit(1);
		}

		Arguments a = new Arguments(args);
		File[] classPath = parseClassPath(a.getValue("cp", "classpath"));
		args = a.getUnnamedValues();
		int startIndex = 1;
		String[] programArguments = new String[args.length - startIndex];
		System.arraycopy(args, startIndex, programArguments, 0,
				programArguments.length);

		VirtualMachine gvm = new VirtualMachine();
		gvm.run(args[0], classPath, programArguments);

	}
}
