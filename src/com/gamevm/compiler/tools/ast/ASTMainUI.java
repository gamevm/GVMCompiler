package com.gamevm.compiler.tools.ast;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JFileChooser;
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
import com.gamevm.compiler.assembly.Translator;
import com.gamevm.compiler.parser.ASTNode;
import com.gamevm.compiler.parser.ClassDeclarationParser;
import com.gamevm.compiler.parser.GCLexer;
import com.gamevm.compiler.parser.GCParser;
import com.gamevm.compiler.translator.TranslationException;
import com.gamevm.compiler.translator.ast.ASTTranslator;
import com.gamevm.compiler.translator.ast.SymbolTable;
import com.gamevm.execution.RuntimeEnvironment;
import com.gamevm.execution.ast.ASTInterpreter;
import com.gamevm.execution.ast.tree.Statement;
import com.gamevm.utils.StringFormatter;

public class ASTMainUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextArea textArea;
	private JTextArea consoleArea;
	private JTree treeView;
	
	private File current;
	private JFileChooser fileChooser;

	private TreeModel astModel;
	private ClassDefinition<ASTNode> classDefAST;
	private ClassDefinition<Statement> classDefTree;

	private void handleException(Exception e) {
		JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}

	private void buildMenu() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = menuBar.add(new JMenu("File"));
		JMenuItem openItem = fileMenu.add("Open File...");
		openItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (fileChooser.showOpenDialog(ASTMainUI.this) == JFileChooser.APPROVE_OPTION) {
						textArea.setText(StringFormatter.readString(fileChooser.getSelectedFile()));
					}
				}catch (FileNotFoundException ex) {
					handleException(ex);
				} catch (IOException ex) {
					handleException(ex);
				}
			}
		});
		JMenuItem saveItem = fileMenu.add("Save");
		saveItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (current == null) {
						if (fileChooser.showSaveDialog(ASTMainUI.this) == JFileChooser.APPROVE_OPTION) {
							current = fileChooser.getSelectedFile();
						} else {
							return;
						}
					}
					
					save(current);
					
				}catch (FileNotFoundException ex) {
					handleException(ex);
				} catch (IOException ex) {
					handleException(ex);
				}
			}
		});
		JMenuItem saveAsItem = fileMenu.add("Save As...");
		saveAsItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (fileChooser.showSaveDialog(ASTMainUI.this) == JFileChooser.APPROVE_OPTION) {
						current = fileChooser.getSelectedFile();
					} else {
						return;
					}
					
					save(current);
					
				}catch (FileNotFoundException ex) {
					handleException(ex);
				} catch (IOException ex) {
					handleException(ex);
				}
			}
		});
		
		
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
		JMenuItem buildTree = compilerMenu.add("AST -> Tree");
		buildTree.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				buildClassTree();
			}
		});

		JMenu runMenu = menuBar.add(new JMenu("Run"));
		JMenuItem interpretASTItem = runMenu.add("Interpret AST");
		interpretASTItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				interpretAST();
			}
		});

		setJMenuBar(menuBar);
	}
	
	private void save(File f) throws IOException {
		FileWriter writer = new FileWriter(f);
		writer.write(textArea.getText());
		writer.close();
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
			classDefAST = classParser.program();
			consoleArea.setText(classDefAST.toString());
		}catch (RecognitionException e) {
			handleException(e);
		}
	}
	
	private void buildClassTree() {
		try {
			buildClassAST();
			SymbolTable s = new SymbolTable(classDefAST.getDeclaration());
			Translator<ASTNode, Statement> t = new ASTTranslator(s);
			classDefTree = new ClassDefinition<Statement>(classDefAST, t);
			consoleArea.setText(classDefTree.toString());
		} catch (TranslationException e) {
			handleException(e);
		}
	}

	private void interpretAST() {
		buildClassTree();
		
		ASTInterpreter interpreter = new ASTInterpreter(new RuntimeEnvironment(System.out, System.err, System.in));
		try {
			interpreter.execute(classDefTree, new String[] {});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private CommonTree getAST() {
		refreshAST();
		return ((CommonTreeModel) treeView.getModel()).getAST();
	}

	public ASTMainUI() {
		super("AST Viewer");
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		fileChooser = new JFileChooser(".");

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
		
		try {
			textArea.setText(StringFormatter.readString(new File("code/experiments/class.gc")));
		}catch (IOException e) {
			handleException(e);
		}

	}

}
