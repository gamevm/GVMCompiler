package com.gamevm.compiler.assembly;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class GClassLoader {
	
	private Collection<File> searchPaths;
	
	public GClassLoader(File... searchPaths) {
		this.searchPaths = new ArrayList<File>();
		this.searchPaths.addAll(Arrays.asList(searchPaths));
	}
	
	protected File getClassFile(String typeName) throws IOException {
		String path = typeName.replace('.', '/') + ".gbc";
		for (File sp : searchPaths) {
			File result = new File(sp, path);
			if (result.exists()) {
				return result;
			}
		}
		throw new IOException("No class file found for type " + typeName);
	}
	
	public ClassFileHeader readHeader(String typeName) throws FileNotFoundException, IOException {
		File cfile = getClassFile(typeName);
		return new ClassFileHeader(new DataInputStream(new FileInputStream(cfile)));
	}
	
	public ClassDeclaration readDeclaration(String typeName) throws FileNotFoundException, IOException {
		File cfile = getClassFile(typeName);
		DataInputStream input = new DataInputStream(new FileInputStream(cfile));
		new ClassFileHeader(input);
		ClassDeclaration declaration = new ClassDeclaration(input);
		input.close();
		return declaration;
	}
	
	public <I extends Instruction> ClassDefinition<I> readDefinition(String typeName, CodeReader<I> reader) throws FileNotFoundException, IOException {
		System.out.println("Reading definition of " + typeName);
		File cfile = getClassFile(typeName);
		InputStream inputStream = new FileInputStream(cfile);
		return new ClassDefinition<I>(inputStream, reader);
	}

}
