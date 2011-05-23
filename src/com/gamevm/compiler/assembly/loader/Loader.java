package com.gamevm.compiler.assembly.loader;

import java.io.IOException;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.ClassFileHeader;
import com.gamevm.compiler.assembly.code.Code;

public interface Loader {
	
	public ClassFileHeader readHeader(String typeName) throws IOException;
	
	public ClassDeclaration readDeclaration(String typeName) throws IOException;
	
	public <C extends Code> ClassDefinition<C> readDefinition(String typeName) throws IOException;

}
