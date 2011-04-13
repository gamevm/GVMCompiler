package com.gamevm.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import com.gamevm.compiler.assembly.Instruction;
import com.gamevm.compiler.assembly.Type;
import com.gamevm.utils.StringFormatter;

public class ASTNode implements Instruction {

	public static final String[] strings = new String[] {"TYPE_BLOCK", "TYPE_WHILE_LOOP", "TYPE_FOR_LOOP", "TYPE_IF", "TYPE_VAR_DECL", "TYPE_ASSIGNMENT", "TYPE_RETURN", "TYPE_METHOD_INVOCATION", "TYPE_OP_LAND", "TYPE_OP_LOR", "TYPE_OP_NEQ", "TYPE_OP_EQU", "TYPE_OP_GTH", "TYPE_OP_LTH", "TYPE_OP_GEQ", "TYPE_OP_LEQ", "TYPE_OP_PLUS", "TYPE_OP_MINUS", "TYPE_OP_MULT", "TYPE_OP_DIV", "TYPE_OP_MOD", "TYPE_OP_NEG", "TYPE_OP_LNEG", "TYPE_LITERAL", "TYPE_VARIABLE", "TYPE_TYPE", "TYPE_NAME", "TYPE_NAME_INDEX", "TYPE_QUALIFIED_ACCESS", "TYPE_ARRAY_ACCESS"};

	public static final int TYPE_BLOCK = 0;
	public static final int TYPE_WHILE_LOOP = 1;
	public static final int TYPE_FOR_LOOP = 2;
	public static final int TYPE_IF = 3;
	public static final int TYPE_VAR_DECL = 4;
	public static final int TYPE_ASSIGNMENT = 5;
	public static final int TYPE_RETURN = 6;
	public static final int TYPE_METHOD_INVOCATION = 7;
	public static final int TYPE_OP_LAND = 8;
	public static final int TYPE_OP_LOR = 9;
	public static final int TYPE_OP_NEQ = 10;
	public static final int TYPE_OP_EQU = 11;
	public static final int TYPE_OP_GTH = 12;
	public static final int TYPE_OP_LTH = 13;
	public static final int TYPE_OP_GEQ = 14;
	public static final int TYPE_OP_LEQ = 15;
	public static final int TYPE_OP_PLUS = 16;
	public static final int TYPE_OP_MINUS = 17;
	public static final int TYPE_OP_MULT = 18;
	public static final int TYPE_OP_DIV = 19;
	public static final int TYPE_OP_MOD = 20;
	public static final int TYPE_OP_NEG = 21;
	public static final int TYPE_OP_LNEG = 22;
	public static final int TYPE_LITERAL = 23;
	public static final int TYPE_VARIABLE = 24;
	public static final int TYPE_TYPE = 25;
	public static final int TYPE_NAME = 26;
	public static final int TYPE_NAME_INDEX = 27;
	public static final int TYPE_QUALIFIED_ACCESS = 28;
	public static final int TYPE_ARRAY_ACCESS = 29;

	private List<ASTNode> children;
	private int type;
	private Object value;
	private Type valueType;

	public ASTNode(int type, ASTNode... children) {
		this.children = new ArrayList<ASTNode>();
		for (ASTNode n : children) {
			this.children.add(n);
		}
		this.type = type;
		this.value = null;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void addNode(ASTNode child) {
		children.add(child);
	}

	public void insertNode(int i, ASTNode child) {
		children.add(i, child);
	}

	public ASTNode getChildAt(int index) {
		return children.get(index);
	}

	public int getChildCount() {
		return children.size();
	}
	
	public Iterable<ASTNode> getChildren() {
		return children;
	}

	public int getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString(int ident) {
		StringBuilder b = new StringBuilder();
		b.append(StringFormatter.generateWhitespaces(ident));
		b.append(strings[type]);
		b.append(String.format(" [%s]", String.valueOf(valueType)));
		if (value != null) {
			b.append(' ');
			b.append(value);
		} else {
			for (ASTNode n : children) {
				b.append('\n');
				b.append(n.toString(ident+2));
			}
		}
		return b.toString();
	}

	@Override
	public String toString() {
		return toString(0);
	}
	
	public void setValueType(Type type) {
		valueType = type;
	}
	
	public Type getValueType() {
		return valueType;
	}

}
