package com.gamevm.compiler.tools.ast;

import java.util.Map;

import javax.swing.JTextArea;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.Field;
import com.gamevm.compiler.assembly.Instruction;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.compiler.assembly.Modifier;
import com.gamevm.compiler.assembly.code.Code;

public class InstructionTextArea extends JTextArea {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static class TextPosition {
		
		int start;
		int length;
		
		public TextPosition(int start, int length) {
			super();
			this.start = start;
			this.length = length;
		}
		
	}
	
	
	private Map<Instruction, TextPosition> positions;
	
	public <C extends Code> void setInstructions(ClassDefinition<C> classDefinition) {
		StringBuilder b = new StringBuilder();

		ClassDeclaration header = classDefinition.getDeclaration();
		
		b.append(Modifier.toString(header.getModifier()));

		b.append("class ");
		b.append(header.getName());

		// TODO: extensions

		b.append("\n\n");
		final Field[] fields = header.getFields();
		final Method[] methods = header.getMethods();
		for (int i = 0; i < fields.length; i++) {
			b.append("  ");
			b.append(fields[i]);
		}

		for (int i = 0; i < methods.length; i++) {
			b.append("  ");
			b.append(methods[i]);
			C code = classDefinition.getImplementation(i);
			if (code != null) {
				b.append('\n');
				b.append(code.toString(4));
			}
			b.append('\n');
		}


		setText(b.toString());
	}

}
