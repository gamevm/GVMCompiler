package com.gamevm.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.assembly.Instruction;
import com.gamevm.execution.NameTable;
import com.gamevm.utils.StringFormatter;

public class ASTNode implements Instruction {

	public static final String[] strings = new String[] { "TYPE_BLOCK", "TYPE_WHILE_LOOP", "TYPE_FOR_LOOP", "TYPE_IF", "TYPE_VAR_DECL",
			"TYPE_ASSIGNMENT", "TYPE_RETURN", "TYPE_METHOD_INVOCATION", "TYPE_OP_NEW", "TYPE_OP_NEW_ARRAY", "TYPE_OP_LAND", "TYPE_OP_LOR",
			"TYPE_OP_NEQ", "TYPE_OP_EQU", "TYPE_OP_GTH", "TYPE_OP_LTH", "TYPE_OP_GEQ", "TYPE_OP_LEQ", "TYPE_OP_PLUS", "TYPE_OP_MINUS",
			"TYPE_OP_MULT", "TYPE_OP_DIV", "TYPE_OP_MOD", "TYPE_OP_NEG", "TYPE_OP_LNEG", "TYPE_LITERAL", "TYPE_VARIABLE", "TYPE_TYPE",
			"TYPE_CLASS_PATH_NODE", "TYPE_QUALIFIED_ACCESS", "TYPE_ARRAY_ACCESS" };

	public static final int TYPE_BLOCK = 0;
	public static final int TYPE_WHILE_LOOP = 1;
	public static final int TYPE_FOR_LOOP = 2;
	public static final int TYPE_IF = 3;
	public static final int TYPE_VAR_DECL = 4;
	public static final int TYPE_ASSIGNMENT = 5;
	public static final int TYPE_RETURN = 6;
	public static final int TYPE_METHOD_INVOCATION = 7;
	public static final int TYPE_OP_NEW = 8;
	public static final int TYPE_OP_NEW_ARRAY = 9;
	public static final int TYPE_OP_LAND = 10;
	public static final int TYPE_OP_LOR = 11;
	public static final int TYPE_OP_NEQ = 12;
	public static final int TYPE_OP_EQU = 13;
	public static final int TYPE_OP_GTH = 14;
	public static final int TYPE_OP_LTH = 15;
	public static final int TYPE_OP_GEQ = 16;
	public static final int TYPE_OP_LEQ = 17;
	public static final int TYPE_OP_PLUS = 18;
	public static final int TYPE_OP_MINUS = 19;
	public static final int TYPE_OP_MULT = 20;
	public static final int TYPE_OP_DIV = 21;
	public static final int TYPE_OP_MOD = 22;
	public static final int TYPE_OP_NEG = 23;
	public static final int TYPE_OP_LNEG = 24;
	public static final int TYPE_LITERAL = 25;
	public static final int TYPE_VARIABLE = 26;
	public static final int TYPE_TYPE = 27;
	public static final int TYPE_CLASS_PATH_NODE = 28;
	public static final int TYPE_QUALIFIED_ACCESS = 29;
	public static final int TYPE_ARRAY_ACCESS = 30;

	private List<ASTNode> children;
	private int type;
	private int startLine;
	private int startPosition;
	private int endLine;
	private int endPosition;
	private Object value;
	private Type valueType;

	public ASTNode(int type, ASTNode... children) {
		this.children = new ArrayList<ASTNode>();
		for (ASTNode n : children) {
			addNode(n);
		}
		this.type = type;
		this.value = null;
	}

	public ASTNode(int type, int startLine, int startPos, int length, Object value) {
		this.children = null;
		this.type = type;
		this.startLine = startLine;
		this.startPosition = startPos;
		this.endLine = startLine;
		this.endPosition = startPos + length;
		this.value = value;
	}

	// public void setType(int type) {
	// this.type = type;
	// }

	public int countNodes(int type) {
		int sum = 0;
		if (children != null) {
			for (ASTNode n : children)
				sum += n.countNodes(type);
		}
		if (this.type == type)
			sum += 1;
		return sum;
	}

	// public void setValue(Object value) {
	// this.value = value;
	// }

	private void updatePositions() {
		startLine = children.get(0).startLine;
		startPosition = children.get(0).startPosition;
		endLine = children.get(children.size() - 1).endLine;
		endPosition = children.get(children.size() - 1).endPosition;
	}

	public void addNode(ASTNode child) {
		if (child != null) { // this check is needed to be able to continue
								// parsing in the presence of parsing errors
			children.add(child);
			updatePositions();
		}
	}

	public void insertNode(int i, ASTNode child) {
		children.add(i, child);
		updatePositions();
	}

	public ASTNode getChildAt(int index) {
		return children.get(index);
	}

	public int getChildCount() {
		if (children == null)
			return 0;
		else
			return children.size();
	}

	public Iterable<ASTNode> getChildren() {
		return children;
	}

	public int getType() {
		return type;
	}

	public int getStartLine() {
		return startLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public void moveStartPositionTo(int line, int pos) {
		startLine = line;
		startPosition = pos;
	}

	public void moveEndPositionTo(int line, int pos) {
		endLine = line;
		endPosition = pos;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public int getEndPosition() {
		return endPosition;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object v) {
		this.value = v;
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
				b.append(n.toString(ident + 2));
			}
		}
		return b.toString();
	}

	@Override
	public String toString() {
		if (value != null)
			return String.format("%s %s (%d:%d-%d:%d)", strings[type], value, startLine, startPosition, endLine, endPosition);
		else
			return String.format("%s (%d:%d-%d:%d)", strings[type], startLine, startPosition, endLine, endPosition);
	}

//	public void setValueType(Type type) {
//		valueType = type;
//	}
//
//	public Type getValueType() {
//		return valueType;
//	}

	public int indexOf(ASTNode child) {
		return children.indexOf(child);
	}

	public boolean isAddressable() {
		switch (type) {
		case TYPE_QUALIFIED_ACCESS:
			return getChildAt(1).getType() == TYPE_VARIABLE;
		case TYPE_VARIABLE:
			return true;
		case TYPE_ARRAY_ACCESS:
			return true;
		default:
			return false;
		}
	}

}
