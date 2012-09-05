package com.gamevm.compiler.tools.nativeclasses;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;

import org.xml.sax.SAXException;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassFileHeader;
import com.gamevm.compiler.assembly.code.Code;
import com.gamevm.utils.commandline.Arguments;

public class NativeClassGenerator {
	
	public static final ClassFileHeader DECLARATION_HEADER = new ClassFileHeader(1, Code.DECLARATION_ONLY);

	public static void generateRuntimeLibrary(List<ClassDeclaration> declarations, File targetDirectory) throws IOException {
		for (ClassDeclaration d : declarations) {
			
			String relPath = d.getName().replace('.', '/') + ".gbc";
			File classFile = new File(targetDirectory, relPath);
			classFile.getParentFile().mkdirs();
			if (!classFile.exists())
				classFile.createNewFile();
			
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(classFile));
			DECLARATION_HEADER.write(output);
			d.write(output);
			output.close();
		}
	}
	
	public static void main(String[] sargs) throws SAXException, IOException {
		
		Arguments args = new Arguments(sargs);
		
		try {
			String runtimeLibDirectoryStr = args.getValue("d", "directory");
			File runtimeLibDirectory = (runtimeLibDirectoryStr != null) ? new File(runtimeLibDirectoryStr) : new File(".");

			XMLNativeReader nativeReader = new XMLNativeReader(new File(args.getUnnamedValues()[0]));
			
			List<ClassDeclaration> classes = nativeReader.read();
			
			generateRuntimeLibrary(classes, runtimeLibDirectory);
			
		} catch (FileNotFoundException e) {
			System.err.println("You must specify a file.");
		} catch (FactoryConfigurationError e) {
			System.err.println("XML reader was not configured correctly (FactoryConfigurationError).");
		} catch (ParserConfigurationException e) {
			System.err.println("XML reader was not configured correctly (ParserConfigurationException).");
		}
		
	}

}
