package com.gamevm.compiler.tools.ast;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ASTViewer {
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		ASTMainUI ui = new ASTMainUI();
		ui.setVisible(true);
	}

}
