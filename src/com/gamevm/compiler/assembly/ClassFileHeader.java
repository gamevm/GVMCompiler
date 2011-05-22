package com.gamevm.compiler.assembly;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.gamevm.compiler.assembly.code.Code;

public class ClassFileHeader {
	
	private int version;
	private int codeType;
	
	public ClassFileHeader(int version, int codeType) {
		this.version = version;
		this.codeType = codeType;
	}
	
//	public ClassFileHeader(int version, Class<? extends Code> codeType) {
//		this.version = version;
//		this.codeType = Code.getCodeIdentifier(codeType);
//	}
	
	public ClassFileHeader(ObjectInputStream stream) throws IOException {
		version = stream.readInt();
		codeType = stream.readInt();
	}
	
	public void write(ObjectOutputStream stream) throws IOException {
		stream.writeInt(version);
		stream.writeInt(codeType);
	}
	
	public int getVersion() {
		return version;
	}
	
	public int getCodeType() {
		return codeType;
	}
	
	public boolean hasDefinition() {
		return codeType != Code.DECLARATION_ONLY;
	}

}
