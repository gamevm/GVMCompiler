package com.gamevm.compiler.assembly.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.ClassFileHeader;
import com.gamevm.compiler.assembly.code.Code;

public abstract class AbstractGBCLoader implements Loader {

	protected abstract File getClassFile(String typeName) throws IOException;
	
	@Override
	public ClassFileHeader readHeader(String typeName) throws FileNotFoundException, IOException {
		File cfile = getClassFile(typeName);
		return new ClassFileHeader(new ObjectInputStream(new FileInputStream(cfile)));
	}
	
	@Override
	public ClassDeclaration readDeclaration(String typeName) throws FileNotFoundException, IOException {
		File cfile = getClassFile(typeName);
		ObjectInputStream input = new ObjectInputStream(new FileInputStream(cfile));
		new ClassFileHeader(input);
		ClassDeclaration declaration = new ClassDeclaration(input);
		input.close();
		return declaration;
	}
	
	@Override
	public <C extends Code> ClassDefinition<C> readDefinition(String typeName) throws FileNotFoundException, IOException {
		System.out.println("Reading definition of " + typeName);
		File cfile = getClassFile(typeName);
		InputStream inputStream = new FileInputStream(cfile);
		return new ClassDefinition<C>(inputStream);
	}

}
