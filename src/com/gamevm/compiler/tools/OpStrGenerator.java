package com.gamevm.compiler.tools;

import javax.swing.JFrame;

public class OpStrGenerator extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2948359866833594183L;

	public OpStrGenerator() {
		super("OpStrGenerator");
		setSize(800,600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		OpStrGenerator window = new OpStrGenerator();
		window.setVisible(true);
	}
	
}
