package com.gamevm.compiler.tools.opstrgen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

public class OpStrGenerator extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2948359866833594183L;
	
	private JTextArea textArea;
	
	private String convert(String input) throws RecognitionException, IOException {
		CharStream charStream = new ANTLRReaderStream(new StringReader(input));
		ConstantDefLexer lexer = new ConstantDefLexer(charStream);
		ConstantDefParser parser = new ConstantDefParser(new CommonTokenStream(lexer));
		
		return parser.constants();
	}
	
	private void buildMenu() {
		
		JMenuBar menu = new JMenuBar();
		JMenu actionsMenu = menu.add(new JMenu("Actions"));
		actionsMenu.add("Convert").addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					textArea.setText(convert(textArea.getText()));
				} catch (RecognitionException e) {
					JOptionPane.showMessageDialog(OpStrGenerator.this, e.getLocalizedMessage(), "Parse Error", JOptionPane.ERROR_MESSAGE);
				} catch (IOException ignore) {
				}
			}});
		
		actionsMenu.add("Generate").addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] elements = textArea.getText().split("\n");
				
				StringBuilder b = new StringBuilder();
				b.append("public static final String[] strings = new String[] {");
				b.append('"');
				b.append(elements[0]);
				b.append('"');
				for (int i = 1; i < elements.length; i++) {
					b.append(", \"");
					b.append(elements[i]);
					b.append('"');
				}
				b.append("};\n\n");
				
				for (int i = 0; i < elements.length; i++) {
					b.append("public static final int ");
					b.append(elements[i]);
					b.append(" = ");
					b.append(i);
					b.append(";\n");
				}
				
				textArea.setText(b.toString());
			}
		});
		
		setJMenuBar(menu);
		
	}

	public OpStrGenerator() {
		super("OpStrGenerator");
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		buildMenu();
		
		textArea = new JTextArea();
		add(new JScrollPane(textArea));
	}

	public static void main(String[] args) throws IOException, RecognitionException {
		OpStrGenerator window = new OpStrGenerator();
		window.setVisible(true);

		
		
	}

}
