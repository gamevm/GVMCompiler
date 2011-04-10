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
import org.antlr.runtime.tree.CommonTreeNodeStream;

import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.parser.ASTNode;
import com.gamevm.compiler.parser.ClassDeclarationParser;
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
		JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	private void buildMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu compilerMenu = menuBar.add(new JMenu("Compiler"));
		JMenuItem refreshASTItem = compilerMenu.add("Refresh Raw AST");
		refreshASTItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshAST();
			}
		});
		JMenuItem buildClassAST = compilerMenu.add("Build Class AST");
		buildClassAST.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				buildClassAST();
			}
		});

		JMenu runMenu = menuBar.add(new JMenu("Run"));
		JMenuItem interpretSourceItem = runMenu.add("Interpret Source");
		interpretSourceItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				interpretSource();
			}
		});

		setJMenuBar(menuBar);
	}

	private void refreshAST() {
		try {
			CharStream charStream = new ANTLRReaderStream(new StringReader(
					textArea.getText()));
			GCLexer lexer = new GCLexer(charStream);
			GCParser parser = new GCParser(new CommonTokenStream(lexer));
			CommonTree tree = (CommonTree) parser.program().getTree();
			TreeModel astModel = new CommonTreeModel(tree);
			treeView.setModel(astModel);
		} catch (IOException ignore) {
		} catch (RecognitionException e) {
			handleException(e);
		}
	}

	private void buildClassAST() {
		try {
			CommonTree tree = getAST();
			ClassDeclarationParser classParser = new ClassDeclarationParser(
					new CommonTreeNodeStream(tree));
			ClassDefinition<ASTNode> classDef = classParser.program();
			consoleArea.setText(classDef.toString());
		}catch (RecognitionException e) {
			handleException(e);
		}
	}

	private void interpretSource() {

	}

	private CommonTree getAST() {
		refreshAST();
		return ((CommonTreeModel) treeView.getModel()).getAST();
	}

	public ASTMainUI() {
		super("AST Viewer");
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		buildMenu();

		treeView = new JTree((TreeModel) null);
		textArea = new JTextArea();
		textArea.setFont(new Font("Courier New", Font.PLAIN, 14));

		consoleArea = new JTextArea();
		consoleArea.setFont(new Font("Courier New", Font.PLAIN, 14));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(600);
		splitPane.setResizeWeight(1.0);
		splitPane.setLeftComponent(new JScrollPane(textArea));
		splitPane.setRightComponent(new JScrollPane(treeView));

		JSplitPane mainPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainPanel.setDividerLocation(400);
		mainPanel.setResizeWeight(1.0);
		mainPanel.setTopComponent(splitPane);
		mainPanel.setBottomComponent(new JScrollPane(consoleArea));
		add(mainPanel);

	}

}
