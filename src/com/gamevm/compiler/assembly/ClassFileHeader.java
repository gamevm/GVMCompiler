package com.gamevm.compiler.assembly;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClassFileHeader {
	
	public static final int CODE_TREE = 0;
	
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
