package com.gamevm.compiler.experiments.jbc.test;

import java.io.IOException;

import com.gamevm.compiler.experiments.jbc.classfile.ClassFile;
import com.gamevm.compiler.experiments.jbc.execution.VirtualMachine;

public class ClassFileTest {
	
	public static void main(String[] args) throws IOException {
		
		ClassFile cf = new ClassFile("bin/com/gamevm/ebc/test/Test.class");
		
		VirtualMachine vm = new VirtualMachine();
		vm.execute(cf);
		
	}

}
