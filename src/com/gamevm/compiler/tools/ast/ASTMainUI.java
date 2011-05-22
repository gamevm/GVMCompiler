package com.gamevm.compiler.tools.ast;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.tree.TreeModel;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.GClassLoader;
import com.gamevm.compiler.assembly.Instruction;
import com.gamevm.compiler.assembly.code.TreeCode;
import com.gamevm.compiler.assembly.code.ExecutableTreeCodeFactory;
import com.gamevm.compiler.parser.ASTNode;
import com.gamevm.compiler.parser.GCASTLexer;
import com.gamevm.compiler.parser.GCASTParser;
import com.gamevm.compiler.translator.TranslationException;
import com.gamevm.compiler.translator.Translator;
import com.gamevm.compiler.translator.TreeCodeTranslator;
import com.gamevm.compiler.translator.ast.SymbolTable;
import com.gamevm.execution.InterpretationListener;
import com.gamevm.execution.Interpreter;
import com.gamevm.execution.RuntimeEnvironment;
import com.gamevm.execution.ast.DebugHandler;
import com.gamevm.execution.ast.DebugModel;
import com.gamevm.execution.ast.Environment;
import com.gamevm.execution.ast.TreeCodeInterpreter;
import com.gamevm.execution.ast.tree.CodeNode;
import com.gamevm.utils.StringFormatter;

public class ASTMainUI extends JFrame implements InterpretationListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextArea textArea;
	private JTextArea consoleArea;
	private JTextArea codeArea;
	private JTable debugArea;
	private JTree treeView;
	
	private File current;
	private JFileChooser fileChooser;

	//private TreeModel astModel;
	private ClassDefinition<TreeCode<ASTNode>> classDefAST;
	private ClassDefinition<TreeCode<CodeNode>> classDefTree;
	
	private Interpreter<?> currentInterpreter;

	private void handleException(Exception e) {
		JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}
	
	private int getTextPosition(int lineNumber, int linePos) {
		try {
			return textArea.getLineStartOffset(lineNumber-1) + linePos;
		} catch (BadLocationException e) {
			handleException(e);
		}
		return 0;
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
//		JMenuItem refreshASTItem = compilerMenu.add("Refresh Raw AST");
//		refreshASTItem.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				refreshAST();
//			}
//		});
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
				interpretAST(false);
			}
		});
		JMenuItem debugASTItem = runMenu.add("Debug AST");
		debugASTItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				interpretAST(true);
			}
		});
		JMenuItem continueItem = runMenu.add("Continue");
		continueItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
		continueItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				currentInterpreter.continueExecution();
			}
		});
		
		JMenuItem abortItem = runMenu.add("Abort");
		abortItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				currentInterpreter.abortExecution();
			}
		});

		setJMenuBar(menuBar);
	}
	
	private void save(File f) throws IOException {
		FileWriter writer = new FileWriter(f);
		writer.write(textArea.getText());
		writer.close();
	}

//	private void refreshAST() {
//		try {
//			CharStream charStream = new ANTLRReaderStream(new StringReader(
//					textArea.getText()));
//			GCLexer lexer = new GCLexer(charStream);
//			GCParser parser = new GCParser(new CommonTokenStream(lexer));
//			CommonTree tree = (CommonTree) parser.program().getTree();
//			TreeModel astModel = new CommonTreeModel(tree);
//			treeView.setModel(astModel);
//		} catch (IOException ignore) {
//		} catch (RecognitionException e) {
//			handleException(e);
//		}
//	}

	private void buildClassAST() {
		try {
//			CommonTree tree = getAST();
//			ClassDeclarationParser classParser = new ClassDeclarationParser(
//					new CommonTreeNodeStream(tree));
			CharStream charStream = new ANTLRReaderStream(new StringReader(
					textArea.getText()));
			GCASTLexer lexer = new GCASTLexer(charStream);
			GCASTParser parser = new GCASTParser(new CommonTokenStream(lexer));
			classDefAST = parser.program();
			codeArea.setText(classDefAST.toDebugString());
			TreeModel astModel = new ASTTreeAdapter(classDefAST);
			treeView.setModel(astModel);
		} catch (RecognitionException e) {
			handleException(e);
		} catch (IllegalArgumentException e) {
			handleException(e);
		} catch (IOException e) {
			handleException(e);
		}
	}
	
	private void buildClassTree() {
		try {
			buildClassAST();
			SymbolTable s = new SymbolTable(classDefAST.getDeclaration(), new GClassLoader(new File("code/bin")));
			Translator<TreeCode<ASTNode>, TreeCode<CodeNode>> t = new TreeCodeTranslator(s);
			classDefTree = new ClassDefinition<TreeCode<CodeNode>>(classDefAST, t, new ExecutableTreeCodeFactory());
			codeArea.setText(classDefTree.toDebugString());
			
			File targetFile = new File("code/bin/" + classDefTree.getDeclaration().getName().replace('.', '/') + ".gbc");
			targetFile.getParentFile().mkdirs();
			OutputStream output = new FileOutputStream(targetFile);
			classDefTree.write(output);
			
		} catch (TranslationException e) {
			handleException(e);
		} catch (IOException e) {
			handleException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private void interpretAST(boolean debug) {
		buildClassTree();
		
		currentInterpreter = new TreeCodeInterpreter(new RuntimeEnvironment(System.out, System.err, System.in));
		currentInterpreter.setDebugMode(debug, new DebugHandler() {
			
			@Override
			public void debug(Instruction i, final ASTNode debugInformation) {
				System.out.format("Debugging: %s (%s)\n", i, debugInformation);
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						
						@Override
						public void run() {
							((DebugModel)debugArea.getModel()).update();
							textArea.requestFocus();
							textArea.setCaretPosition(getTextPosition(debugInformation.getStartLine(), debugInformation.getStartPosition()));
							textArea.moveCaretPosition(getTextPosition(debugInformation.getEndLine(), debugInformation.getEndPosition()));
						}
					});
				} catch (InterruptedException e) {
				} catch (InvocationTargetException e) {
				}
				
			}
		});
		
		try {
			textArea.setEditable(false);
			((Interpreter<TreeCode<CodeNode>>)currentInterpreter).execute(classDefTree, new String[] {}, this, new GClassLoader(new File("code/bin")));
			debugArea.setModel(new DebugModel(Environment.getInstance()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	private CommonTree getAST() {
//		refreshAST();
//		return ((CommonTreeModel) treeView.getModel()).getAST();
//	}

	public ASTMainUI() {
		super("AST Viewer");
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		fileChooser = new JFileChooser(".");

		buildMenu();

		treeView = new JTree((TreeModel) null);
		treeView.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				Object o = e.getPath().getLastPathComponent();
				if (o instanceof ASTNode) {
					ASTNode n = (ASTNode)o;
					textArea.requestFocus();
					textArea.setCaretPosition(getTextPosition(n.getStartLine(), n.getStartPosition()));
					textArea.moveCaretPosition(getTextPosition(n.getEndLine(), n.getEndPosition()));
					//textArea.select(n.getStartPosition(), n.getEndPosition());
				}
			}});
		
		
		textArea = new JTextArea();
		textArea.setFont(new Font("Courier New", Font.PLAIN, 14));

		codeArea = new JTextArea();
		codeArea.setEditable(false);
		codeArea.setFont(new Font("Courier New", Font.PLAIN, 14));
		
		consoleArea = new JTextArea();
		consoleArea.setFont(new Font("Courier New", Font.PLAIN, 14));
		
		debugArea = new JTable();
		
		JTabbedPane bottomPanel = new JTabbedPane();
		bottomPanel.addTab("Code", new JScrollPane(codeArea));
		bottomPanel.addTab("Console", new JScrollPane(consoleArea));
		bottomPanel.addTab("Debug", new JScrollPane(debugArea));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(600);
		splitPane.setResizeWeight(1.0);
		splitPane.setLeftComponent(new JScrollPane(textArea));
		splitPane.setRightComponent(new JScrollPane(treeView));

		JSplitPane mainPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainPanel.setDividerLocation(400);
		mainPanel.setResizeWeight(1.0);
		mainPanel.setTopComponent(splitPane);
		mainPanel.setBottomComponent(bottomPanel);
		add(mainPanel);
		
		try {
			textArea.setText(StringFormatter.readString(new File("code/experiments/class.gc")));
		}catch (IOException e) {
			handleException(e);
		}

	}

	@Override
	public void finished() {
		textArea.setEditable(true);
		textArea.setSelectionStart(0);
		textArea.setSelectionEnd(0);
		debugArea.setModel(new DefaultTableModel());
	}

}
