package com.gamevm.compiler.tools.ast;

import java.awt.Font;
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
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;

import com.gamevm.compiler.parser.GCLexer;
import com.gamevm.compiler.parser.GCParser;

public class ASTMainUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextArea textArea;
	private JTextArea consoleArea;
	private JTree treeView;
	
	private TreeModel astModel;
	
	private void handleException(Exception e) {
		JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	private void buildMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu compilerMenu = menuBar.add(new JMenu("Compiler"));
		JMenuItem buildASTItem = compilerMenu.add("Build AST");
		buildASTItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				buildAST();
			}});
		
		
		JMenu runMenu = menuBar.add(new JMenu("Run"));
		JMenuItem interpretSourceItem = runMenu.add("Interpret Source");
		interpretSourceItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				interpretSource();
			}});
		
		setJMenuBar(menuBar);
	}
	
	private void buildAST() {
		try {
			CharStream charStream = new ANTLRReaderStream(new StringReader(textArea.getText()));
			GCLexer lexer = new GCLexer(charStream);
			GCParser parser = new GCParser(new CommonTokenStream(lexer));
			CommonTree tree = (CommonTree)parser.program().getTree();
			TreeModel astModel = new CommonTreeModel(tree);
			treeView.setModel(astModel);
		} catch (IOException ignore) {
		} catch (RecognitionException e) {
			handleException(e);
		}
	}
	
	private void interpretSource() {
		
	}
	
	
	public ASTMainUI() {
		super("AST Viewer");
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		buildMenu();
		
		treeView = new JTree();
		textArea = new JTextArea();
		textArea.setFont(new Font("Courier New", Font.PLAIN, 14));
		
		consoleArea = new JTextArea();
		consoleArea.setFont(new Font("Courier New", Font.PLAIN, 14));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setLeftComponent(new JScrollPane(textArea));
		splitPane.setRightComponent(new JScrollPane(treeView));
		
		JSplitPane mainPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainPanel.setTopComponent(splitPane);
		mainPanel.setBottomComponent(consoleArea);
		add(mainPanel);
	}

}
