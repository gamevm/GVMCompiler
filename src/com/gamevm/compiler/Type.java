package com.gamevm.compiler;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Type {

	public static final int ORDINAL_BYTE = 0;
	public static final int ORDINAL_CHAR = 1;
	public static final int ORDINAL_SHORT = 2;
	public static final int ORDINAL_INT = 3;
	public static final int ORDINAL_LONG = 4;
	public static final int ORDINAL_FLOAT = 5;
	public static final int ORDINAL_DOUBLE = 6;
	public static final int ORDINAL_BOOLEAN = -1;
	public static final int ORDINAL_VOID = Integer.MIN_VALUE;

	public static final Type VOID = new Type("_void", null, null);
	public static final Type STRING = new Type("gc.String", null, null);
	public static final Type DOUBLE = new Type("_double", 0.0, STRING);
	public static final Type FLOAT = new Type("_float", 0.0f, DOUBLE);
	public static final Type LONG = new Type("_long", 0, FLOAT);
	public static final Type INT = new Type("_int", 0, LONG);
	public static final Type SHORT = new Type("_short", 0, INT);
	public static final Type BYTE = new Type("_byte", 0, SHORT);
	public static final Type CHAR = new Type("_char", '\0', SHORT);
	public static final Type BOOLEAN = new Type("_boolean", false, null);

	public static final Type[] IMPLICIT_IMPORTS = new Type[] { STRING, new Type("gc.System", null, null) };

	private static Map<String, Type> typePool;
	private static String currentPackage;

	static {
		typePool = new HashMap<String, Type>();

		typePool.put(VOID.getName(), VOID);
		typePool.put(BYTE.getName(), BYTE);
		typePool.put(SHORT.getName(), SHORT);
		typePool.put(INT.getName(), INT);
		typePool.put(LONG.getName(), LONG);
		typePool.put(FLOAT.getName(), FLOAT);
		typePool.put(DOUBLE.getName(), DOUBLE);
		typePool.put(BOOLEAN.getName(), BOOLEAN);
		typePool.put(CHAR.getName(), CHAR);

		for (Type t : IMPLICIT_IMPORTS) {
			typePool.put(t.getName(), t);
		}

		// create primitive type hierachy:

	}

	private String name;
	private Object defaultValue;
	private boolean isPrimitive;
	private Type parent;

	public static void setCurrentPackage(String p) {
		currentPackage = p;
	}

	private Type(String name, Object defaultValue, Type parent) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.isPrimitive = (name.charAt(0) == '_');
		this.parent = parent;
	}

	public static Type importType(String typeName) {
		Type t = new Type(typeName, null, null);
		if (!typePool.containsKey(typeName))
			typePool.put(typeName, t);
		return t;
	}

	public static Collection<Type> getRegisteredClasses() {
		Collection<Type> result = new ArrayList<Type>();
		for (Type t : typePool.values()) {
			if (t.getName().startsWith("_"))
				continue;
			if (t.getName().endsWith("[]"))
				continue;

			result.add(t);
		}
		return result;
	}

	protected static Type getType(String name, boolean add) {
		if (name.endsWith("[]") && !add)
			return getArrayType(getType(getElement(name)), getDimension(name));

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
						if (!add)
							return null;
						else {
							t = new Type(name, null, null);
							typePool.put(name, t);
						}

					}
				}
			}

		}
		return t;
	}

	public static boolean isType(String name) {
		return (getType(name, false) != null);
	}

	public static Type getType(String name) {
		Type t = getType(name, false);
		if (t == null)
			throw new IllegalArgumentException(String.format("Unknown type: %s", name));
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
			throw new IllegalArgumentException("Invalid hierachy value " + hierachyValue);
		}
	}

	public static Type getCommonType(Type a, Type b) {
		if (a == b)
			return a;
		else if (a == null || b == null) {
			String an = (a == null) ? "void" : a.toString();
			String bn = (b == null) ? "void" : b.toString();
			throw new IllegalArgumentException(String.format("Types are not compatible: %s and %s", an, bn));
		} else {
			if (a.isAssignmentCompatibleTo(b)) {
				return b;
			} else if (b.isAssignmentCompatibleTo(a)) {
				return a;
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
		throw new IllegalArgumentException("Literal of type " + literalValue.getClass().getName() + " is not supported.");
	}

	public static Type getArrayType(Type type, int dimension) {
		StringBuilder b = new StringBuilder();
		b.append(type.getName());
		for (int i = 0; i < dimension; i++) {
			b.append("[]");
		}
		return getType(b.toString(), true);
	}

	public boolean isArrayType() {
		return isArrayType(name);
	}

	protected static boolean isArrayType(String name) {
		return name.endsWith("[]");
	}

	protected static int getDimension(String name) {
		return (name.length() - name.indexOf('[')) / 2;
	}

	public int getDimension() {
		return getDimension(name);
	}

	protected static String getElement(String name) {
		return name.substring(0, name.indexOf('['));
	}

	public Type getElementType() {
		return getType(getElement(name));
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

	public boolean isAssignmentCompatibleTo(Type t) {
		if (t == this)
			return true;
		return parent.isAssignmentCompatibleTo(t);
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
