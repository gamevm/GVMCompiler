package com.gamevm.compiler.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.code.ExecutableTreeCodeFactory;
import com.gamevm.compiler.assembly.code.TreeCode;
import com.gamevm.compiler.assembly.loader.GBCDirectoryLoader;
import com.gamevm.compiler.assembly.runtime.RuntimeClasses;
import com.gamevm.compiler.parser.ASTNode;
import com.gamevm.compiler.parser.GCASTLexer;
import com.gamevm.compiler.parser.GCASTParser;
import com.gamevm.compiler.parser.ParserError;
import com.gamevm.compiler.translator.TranslationException;
import com.gamevm.compiler.translator.Translator;
import com.gamevm.compiler.translator.TreeCodeTranslator;
import com.gamevm.compiler.translator.ast.SymbolTable;
import com.gamevm.execution.ast.tree.CodeNode;

public class Compiler {
	
	public static void main(String[] args) throws IOException, RecognitionException, TranslationException {
		
		int errorCount = 0;
		
		if (args.length == 0)
			System.out.println("Please pass at least one file.");
		
		final File binFolder = new File("code/bin");
		if (!binFolder.exists())
			binFolder.mkdir();
		
		RuntimeClasses.generateRuntimeLibrary(binFolder);
		
		for (String filename : args) {
			
			final File file = new File(filename);
			CharStream charStream = new ANTLRInputStream(new FileInputStream(file));
			GCASTLexer lexer = new GCASTLexer(charStream);
			GCASTParser parser = new GCASTParser(new CommonTokenStream(lexer));
			ClassDefinition<TreeCode<ASTNode>> ast = parser.program();
			
			List<ParserError> errors = parser.getErrors();
			for (ParserError e : errors) {
				System.out.println(e.getMessage(parser));
				e.printStackTrace();
			}
			
			
			Translator<TreeCode<ASTNode>, TreeCode<CodeNode>> translator = new TreeCodeTranslator(new SymbolTable(ast.getDeclaration(), new GBCDirectoryLoader(binFolder)));
			ClassDefinition<TreeCode<CodeNode>> statements = new ClassDefinition<TreeCode<CodeNode>>(ast, translator, new ExecutableTreeCodeFactory());
		
			System.out.println(statements.toDebugString());
			
			List<TranslationException> transErrors = translator.getErrors();
			for (TranslationException e : transErrors) {
				System.out.format("%s (%d,%d): %s\n", file.getName(), e.getNode().getStartLine(), e.getNode().getStartPosition(), e.getLocalizedMessage());
				e.printStackTrace();
			}
			
			if (errors.size() == 0 && transErrors.size() == 0) {
				String name = statements.getDeclaration().getName();
				File classFile = new File(binFolder, name.replace('.', '/') + ".gbc");
				classFile.getParentFile().mkdirs();
				if (!classFile.exists())
					classFile.createNewFile();
				OutputStream output = new FileOutputStream(classFile);
				System.out.println("Writing " + classFile.getName() + " ...");
				statements.write(output);	
			}
			
			errorCount = errorCount + errors.size() + transErrors.size();
			
		}	
		System.out.format("Compilation finished with %d errors.\n", errorCount);
	}

}
