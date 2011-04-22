package com.gamevm.execution.ast.tree;

import com.gamevm.compiler.parser.ASTNode;

public class Operator {
	
	private Operator() {}
	
	public static String getOperatorString(int astType) {
		switch (astType) {
		case ASTNode.TYPE_OP_GTH:
			return ">";
		case ASTNode.TYPE_OP_LTH:
			return "<";
		case ASTNode.TYPE_OP_GEQ:
			return ">=";
		case ASTNode.TYPE_OP_LEQ:
			return "<=";
		case ASTNode.TYPE_OP_PLUS:
			return "+";
		case ASTNode.TYPE_OP_MINUS:
			return "-";
		case ASTNode.TYPE_OP_MULT:
			return "*";
		case ASTNode.TYPE_OP_DIV:
			return "/";
		case ASTNode.TYPE_OP_MOD:
			return "%";
		case ASTNode.TYPE_OP_LAND:
			return "&&";
		case ASTNode.TYPE_OP_LOR:
			return "||";
		case ASTNode.TYPE_OP_NEG:
			return "-";
		case ASTNode.TYPE_OP_LNEG:
			return "!";
		default:
			return "";	
		}
	}

}
