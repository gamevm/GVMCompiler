package com.gamevm.ebc.bcfile;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ClassFile {

	public static final byte[] MAGIC = new byte[] { (byte) 0xCA, (byte) 0xFE,
			(byte) 0xBA, (byte) 0xBE };

	private int minorVersion;
	private int majorVersion;

	private int accessFlags;

	// Indices to the constant pool:
	private int thisClass;
	private int superClass;
	private int[] interfaces;

	private Constant[] constantPool;
	private Field[] fields;
	private Method[] methods;
	private Attribute[] attributes;

	public ClassFile(InputStream file) throws IOException {
		read(new DataInputStream(file));
	}

	public ClassFile(File file) throws IOException {
		this(new FileInputStream(file));
	}

	public ClassFile(String file) throws IOException {
		this(new FileInputStream(file));
	}
	
	public Method getMethod(String methodName) {
		int index = findString(methodName);
		for (Method m : methods) {
			if (m.getNameIndex() == index)
				return m;
		}
		return null;
	}
	
	protected int findString(String value) {
		for (int i = 0; i < constantPool.length; i++) {
			if ((constantPool[i] instanceof ConstantUTF) && ((ConstantUTF)constantPool[i]).getValue().equals(value)) {
				return i;
			}
		}
		return -1;
	}

	protected void read(DataInputStream istream) throws IOException {
		byte[] magic = new byte[4];
		istream.readFully(magic);
		if (magic[0] != MAGIC[0] || magic[1] != MAGIC[1]
				|| magic[2] != MAGIC[2] || magic[3] != MAGIC[3])
			throw new IllegalArgumentException(
					"File is not a valid .class file");

		minorVersion = istream.readUnsignedShort();
		majorVersion = istream.readUnsignedShort();

		int constantPoolSize = istream.readUnsignedShort();
		constantPool = new Constant[constantPoolSize];
		for (int i = 1; i < constantPool.length; i++) {
			constantPool[i] = readConstant(istream);
		}

		accessFlags = istream.readUnsignedShort();
		thisClass = istream.readUnsignedShort();
		superClass = istream.readUnsignedShort();

		int interfacesCount = istream.readUnsignedShort();
		interfaces = new int[interfacesCount];
		for (int i = 0; i < interfaces.length; i++) {
			interfaces[i] = istream.readUnsignedShort();
		}

		int fieldsCount = istream.readUnsignedShort();
		fields = new Field[fieldsCount];
		for (int i = 0; i < fields.length; i++) {
			fields[i] = readField(istream);
		}

		int methodsCount = istream.readUnsignedShort();
		methods = new Method[methodsCount];
		for (int i = 0; i < methods.length; i++) {
			methods[i] = readMethod(istream);
		}

		int attributesCount = istream.readUnsignedShort();
		attributes = new Attribute[attributesCount];
		for (int i = 0; i < attributes.length; i++) {
			attributes[i] = readAttribute(istream);
		}

	}

	protected Field readField(DataInputStream istream) throws IOException {

		int accessFlag = istream.readUnsignedShort();
		int nameIndex = istream.readUnsignedShort();
		int descriptorIndex = istream.readUnsignedShort();

		int attributesCount = istream.readUnsignedShort();
		Attribute[] attributes = new Attribute[attributesCount];
		for (int i = 0; i < attributesCount; i++) {
			attributes[i] = readAttribute(istream);
		}

		return new Field(accessFlag, nameIndex, descriptorIndex, attributes);

	}

	protected Method readMethod(DataInputStream istream) throws IOException {

		int accessFlag = istream.readUnsignedShort();
		int nameIndex = istream.readUnsignedShort();
		int descriptorIndex = istream.readUnsignedShort();

		int attributesCount = istream.readUnsignedShort();
		Attribute[] attributes = new Attribute[attributesCount];
		for (int i = 0; i < attributesCount; i++) {
			attributes[i] = readAttribute(istream);
		}

		return new Method(accessFlag, nameIndex, descriptorIndex, attributes);

	}

	protected Attribute readAttribute(DataInputStream istream)
			throws IOException {

		int attributeNameIndex = istream.readUnsignedShort();

		long length = readUnsignedInt(istream);

		String attributeName = getString(attributeNameIndex);

		if (attributeName.equals("ConstantValue")) {

			int constantValueIndex = istream.readUnsignedShort();
			return new ConstantValueAttribute(constantValueIndex);

		} else if (attributeName.equals("Code")) {

			int maxStack = istream.readUnsignedShort();
			int maxLocals = istream.readUnsignedShort();

			long codeSize = readUnsignedInt(istream);
			byte[] code = new byte[(int)codeSize]; // TODO: allow more than 2.000.000.000 loc ;)

			istream.readFully(code);

			int exceptionTableSize = istream.readUnsignedShort();
			CodeAttribute.Exception[] exceptionTable = new CodeAttribute.Exception[exceptionTableSize];
			for (int i = 0; i < exceptionTable.length; i++) {
				int startPc = istream.readUnsignedShort();
				int endPc = istream.readUnsignedShort();
				int handlerPc = istream.readUnsignedShort();
				int catchType = istream.readUnsignedShort();
				exceptionTable[i] = new CodeAttribute.Exception(startPc, endPc,
						handlerPc, catchType);
			}
			
			int attributesCount = istream.readUnsignedShort();
			for (int i = 0; i < attributesCount; i++) {
				readAttribute(istream);
			}

			return new CodeAttribute(maxStack, maxLocals, code, exceptionTable);

		} else if (attributeName.equals("Exceptions")) {

			int exceptionCount = istream.readUnsignedShort();
			int[] exceptionIndices = new int[exceptionCount];
			for (int i = 0; i < exceptionIndices.length; i++) {
				exceptionIndices[i] = istream.readUnsignedShort();
			}

			return new ExceptionsAttribute(exceptionIndices);

		} else {
			istream.skip(length);
			return null;
		}

	}

	protected Constant readConstant(DataInputStream istream) throws IOException {

		int tag = istream.readUnsignedByte();

		switch (tag) {
		case Constant.CONSTANT_CLASS:
			return new ConstantClassInfo(istream.readUnsignedShort());
		case Constant.CONSTANT_FIELD_REF:
			return new ConstantFieldRef(istream.readUnsignedShort(),
					istream.readUnsignedShort());
		case Constant.CONSTANT_METHOD_REF:
			return new ConstantMethodRef(istream.readUnsignedShort(),
					istream.readUnsignedShort());
		case Constant.CONSTANT_INTERFACE_METHOD_REF:
			return new ConstantInterfaceMethodRef(istream.readUnsignedShort(),
					istream.readUnsignedShort());
		case Constant.CONSTANT_STRING:
			return new ConstantString(istream.readUnsignedShort());
		case Constant.CONSTANT_FLOAT:
			return new ConstantFloat(istream.readFloat());
		case Constant.CONSTANT_LONG:
			return new ConstantLong(istream.readLong());
		case Constant.CONSTANT_DOUBLE:
			return new ConstantDouble(istream.readDouble());
		case Constant.CONSTANT_NAME_AND_TYPE:
			return new ConstantNameAndType(istream.readUnsignedShort(),
					istream.readUnsignedShort());
		case Constant.CONSTANT_UTF_8:
			return new ConstantUTF(istream.readUTF());
		default:
			throw new IllegalArgumentException("Unknown constant TAG " + tag);
		}

	}

	protected String getString(int index) {
		return ((ConstantUTF) constantPool[index]).getValue();
	}

	private long readUnsignedInt(DataInputStream input) throws IOException {
		int a = input.readUnsignedByte();
		int b = input.readUnsignedByte();
		int c = input.readUnsignedByte();
		int d = input.readUnsignedByte();
		return (((a & 0xff) << 24) | ((b & 0xff) << 16)
				| ((c & 0xff) << 8) | (d & 0xff));

	}

}
