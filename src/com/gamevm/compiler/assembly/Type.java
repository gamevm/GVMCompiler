package com.gamevm.compiler.assembly;

import java.util.HashMap;
import java.util.Map;

import com.gamevm.compiler.translator.TranslationException;

public class Type {
	
	public static final int ORDINAL_BYTE = 0;
	public static final int ORDINAL_SHORT = 1;
	public static final int ORDINAL_INT = 2;
	public static final int ORDINAL_LONG = 3;
	public static final int ORDINAL_FLOAT = 4;
	public static final int ORDINAL_DOUBLE = 5;
	public static final int ORDINAL_BOOLEAN = -1;
	public static final int ORDINAL_CHAR = -2;

	public static final Type BYTE = new Type("_byte", 0, ORDINAL_BYTE);
	public static final Type SHORT = new Type("_short", 0, ORDINAL_SHORT);
	public static final Type INT = new Type("_int", 0, ORDINAL_INT);
	public static final Type LONG = new Type("_long", 0, ORDINAL_LONG);
	public static final Type FLOAT = new Type("_float", 0.0f, ORDINAL_FLOAT);
	public static final Type DOUBLE = new Type("_double", 0.0, ORDINAL_DOUBLE);
	public static final Type BOOLEAN = new Type("_boolean", false, ORDINAL_BOOLEAN);
	public static final Type CHAR = new Type("_char", '\0', ORDINAL_CHAR);

	private static Map<String, Type> typePool;
	private static String currentPackage;

	static {
		typePool = new HashMap<String, Type>();
		// typePool.put("_byte", new Type("_byte", 0));
		// typePool.put("_short", new Type("_short", 0));
		// typePool.put("_int", new Type("_int", 0));
		// typePool.put("_long", new Type("_long", 0));
		// typePool.put("_float", new Type("_float", 0.0f));
		// typePool.put("_double", new Type("_double", 0.0));
		// typePool.put("_boolean", new Type("_boolean", false));
		// typePool.put("_char", new Type("_char", '\0'));

		typePool.put(BYTE.getName(), BYTE);
		typePool.put(SHORT.getName(), SHORT);
		typePool.put(INT.getName(), INT);
		typePool.put(LONG.getName(), LONG);
		typePool.put(FLOAT.getName(), FLOAT);
		typePool.put(DOUBLE.getName(), DOUBLE);
		typePool.put(BOOLEAN.getName(), BOOLEAN);
		typePool.put(CHAR.getName(), CHAR);
	}

	private String name;
	private Object defaultValue;
	private boolean isPrimitive;
	private int hierachyValue;

	public static void setCurrentPackage(String p) {
		currentPackage = p;
	}

	private Type(String name, Object defaultValue, int hierachyValue) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.isPrimitive = (name.charAt(0) == '_');
		this.hierachyValue = hierachyValue;
	}
	
	public int ordinal() {
		return hierachyValue;
	}

	public static void importType(String typeName) {
		typePool.put(typeName, new Type(typeName, null, -3));
	}

	public static Type getType(String name) {
		Type t = typePool.get(name);
		if (t == null) {

			// resolve abbreviations:
			t = typePool.get("gc." + name);
			if (t == null) {

				t = typePool.get(currentPackage + "." + name);

				if (t == null) {

					if (!name.contains(".")) {
						for (Type tp : typePool.values()) {
							if (tp.getName().endsWith(name)) {
								t = tp;
								break;
							}
						}
					}

					if (t == null) {
						t = new Type(name, null, -3);
						typePool.put(name, t);
					}
				}
			}

		}
		return t;
	}

	public static Type getPrimitiveType(int hierachyValue) {
		switch (hierachyValue) {
		case 0:
			return BYTE;
		case 1:
			return SHORT;
		case 2:
			return INT;
		case 3:
			return LONG;
		case 4:
			return FLOAT;
		case 5:
			return DOUBLE;
		default:
			throw new IllegalArgumentException("Invalid hierachy value "
					+ hierachyValue);
		}
	}
	
	public static Type getCommonType(Type a, Type b) {
		if (a == b)
			return a;
		else if (a == null || b == null) {
			String an = (a == null) ? "void" : a.toString();
			String bn = (b == null) ? "void" : b.toString();
			throw new IllegalArgumentException(String.format("Types are not compatible: %s and %s", an, bn));
		}
		else {
			if (a.hierachyValue >= 0 && b.hierachyValue >= 0) {
				if (a.hierachyValue > b.hierachyValue)
					return a;
				else
					return b;
			} else {
				throw new IllegalArgumentException(String.format("Types are not compatible: %s and %s", a, b));
			}
		}
	}

	public static Type getLiteralType(Object literalValue) {
		if (literalValue instanceof Byte) {
			return BYTE;
		} else if (literalValue instanceof Short) {
			return SHORT;
		} else if (literalValue instanceof Integer) {
			return INT;
		} else if (literalValue instanceof Long) {
			return LONG;
		} else if (literalValue instanceof Float) {
			return FLOAT;
		} else if (literalValue instanceof Double) {
			return DOUBLE;
		} else if (literalValue instanceof Boolean) {
			return BOOLEAN;
		} else if (literalValue instanceof Character) {
			return CHAR;
		}
		throw new IllegalArgumentException("Literal of type "
				+ literalValue.getClass().getName() + " is not supported.");
	}

	public static Type getArrayType(Type type, int dimension) {
		StringBuilder b = new StringBuilder();
		b.append(type.getName());
		for (int i = 0; i < dimension; i++) {
			b.append("[]");
		}
		return getType(b.toString());
	}

	public static Type getElementType(Type arrayType) {
		return getType(arrayType.getName().substring(0,
				arrayType.getName().indexOf('[')));
	}

	public String getName() {
		return name;
	}

	public boolean isPrimitive() {
		return isPrimitive;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public boolean isAssignmentCompatible(Type t) {
		if (hierachyValue >= 0) {
			return t.hierachyValue >= hierachyValue;
		} else {
			// TODO: add inheritance support here
			return name.equals(t.name);
		}
	}

	@Override
	public boolean equals(Object obj) {
		try {
			return name.equals(((Type) obj).name);
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		if (!isPrimitive) {
			return name;
		} else {
			return name.substring(1);
		}
	}
}
