package com.gamevm.execution.ast.tree;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.parser.ASTNode;

public class Operator {
	
	private Operator() {}
	
	public static String getOperatorString(int op) {
		switch (op) {
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
		case ASTNode.TYPE_OP_NEQ:
			return "!=";
		case ASTNode.TYPE_OP_EQU:
			return "==";
		default:
			throw new IllegalArgumentException(op + " is not an operator");
		}
	}
	
	public static boolean typeIsValidForOperator(Type t, int op) {
		switch (op) {
		case ASTNode.TYPE_OP_LAND:
		case ASTNode.TYPE_OP_LOR:
		case ASTNode.TYPE_OP_LNEG:
			return t.isAssignmentCompatibleTo(Type.BOOLEAN);
		case ASTNode.TYPE_OP_NEQ:
		case ASTNode.TYPE_OP_EQU:
			return true;
		case ASTNode.TYPE_OP_GTH:
		case ASTNode.TYPE_OP_LTH:
		case ASTNode.TYPE_OP_GEQ:
		case ASTNode.TYPE_OP_LEQ:
		case ASTNode.TYPE_OP_PLUS:
		case ASTNode.TYPE_OP_MINUS:
		case ASTNode.TYPE_OP_MULT:
		case ASTNode.TYPE_OP_DIV:
		case ASTNode.TYPE_OP_MOD:
			return t.isNumeric();
		default:
			throw new IllegalArgumentException(op + " is not an operator");
		}
	}
	
	public static String getDesiredTypeDescription(int op) {
		switch (op) {
		case ASTNode.TYPE_OP_LAND:
		case ASTNode.TYPE_OP_LOR:
		case ASTNode.TYPE_OP_LNEG:
			return "boolean";
		case ASTNode.TYPE_OP_GTH:
		case ASTNode.TYPE_OP_LTH:
		case ASTNode.TYPE_OP_GEQ:
		case ASTNode.TYPE_OP_LEQ:
		case ASTNode.TYPE_OP_PLUS:
		case ASTNode.TYPE_OP_MINUS:
		case ASTNode.TYPE_OP_MULT:
		case ASTNode.TYPE_OP_DIV:
		case ASTNode.TYPE_OP_MOD:
			return "numeric";
		default:
			throw new IllegalArgumentException(op + " is not an operator");
		}
	}
	
	public static Type getResultType(int op, Type leftType, Type rightType) {
		switch (op) {
		case ASTNode.TYPE_OP_LAND:
		case ASTNode.TYPE_OP_LOR:
		case ASTNode.TYPE_OP_LNEG:
		case ASTNode.TYPE_OP_GTH:
		case ASTNode.TYPE_OP_LTH:
		case ASTNode.TYPE_OP_GEQ:
		case ASTNode.TYPE_OP_LEQ:
		case ASTNode.TYPE_OP_EQU:
		case ASTNode.TYPE_OP_NEQ:
			return Type.BOOLEAN;
		case ASTNode.TYPE_OP_PLUS:
		case ASTNode.TYPE_OP_MINUS:
		case ASTNode.TYPE_OP_MULT:
		case ASTNode.TYPE_OP_DIV:
		case ASTNode.TYPE_OP_MOD:
			return Type.getCommonType(leftType, rightType);
		default:
			throw new IllegalArgumentException(op + " is not an operator");
		}
	}

}
