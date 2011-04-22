package com.gamevm.compiler.assembly;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.gamevm.compiler.parser.ASTNode;
import com.gamevm.execution.ast.tree.Statement;

public class ClassFileHeader {
	
	public static final int AST_TREE = 0;
	public static final int CODE_TREE = 1;
	public static final int DECLARATION_ONLY = 2;
	
	public static final int MAX_CODE_TYPE = 2;
	
	private static Map<Class<? extends Instruction>, Integer> class2CodeType = new HashMap<Class<? extends Instruction>, Integer>();
	
	static {
		class2CodeType.put(ASTNode.class, AST_TREE);
		class2CodeType.put(Statement.class, CODE_TREE);
	}
	
	private int version;
	private int codeType;
	
	public ClassFileHeader(int version, int codeType) {
		this.version = version;
		this.codeType = codeType;
	}
	
	public ClassFileHeader(DataInputStream stream) throws IOException {
		version = stream.readInt();
		codeType = stream.readInt();
	}
	
	public ClassFileHeader(int version, Class<? extends Instruction> instructionType) {
		this.version = version;
		this.codeType = class2CodeType.get(instructionType);
	}
	
	public void write(DataOutputStream stream) throws IOException {
		stream.writeInt(version);
		stream.writeInt(codeType);
	}
	
	public int getVersion() {
		return version;
	}
	
	public int getCodeType() {
		return codeType;
	}

}
