package com.gamevm.ebc.test;

import java.io.IOException;

import com.gamevm.ebc.bcfile.ClassFile;
import com.gamevm.ebc.execution.VirtualMachine;

public class ClassFileTest {
	
	public static void main(String[] args) throws IOException {
		
		ClassFile cf = new ClassFile("bin/com/gamevm/ebc/test/Test.class");
		
		VirtualMachine vm = new VirtualMachine();
		vm.execute(cf);
		
	}

}
