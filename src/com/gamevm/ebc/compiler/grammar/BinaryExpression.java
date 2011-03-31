package com.gamevm.ebc.compiler.grammar;

import java.util.Collection;
import java.util.Map;

public class BinaryExpression implements Expression {

	private Expression a;
	private Expression b;
	private int operator;
	
	public static final int OPERATOR_ADD = 0;
	public static final int OPERATOR_SUB = 1;
	public static final int OPERATOR_MUL = 2;
	public static final int OPERATOR_DIV = 3;
	public static final int OPERATOR_MOD = 4;
	public static final int OPERATOR_EQU = 5;
	public static final int OPERATOR_NEQ = 6;
	public static final int OPERATOR_GEQ = 7;
	public static final int OPERATOR_LEQ = 8;
	public static final int OPERATOR_GTH = 9;
	public static final int OPERATOR_LTH = 10;
	public static final int OPERATOR_LAN =11;
	public static final int OPERATOR_LOR = 12;

	
	public BinaryExpression(Expression a, Expression b, int operator) {
		this.a = b;
		this.b = a;
		this.operator = operator;
	}
	
	@Override
	public Object evaluate(Map<String, Object> environment) {
		switch (operator) {
		case OPERATOR_ADD:
			Object aVal = a.evaluate(environment);
			Object bVal = b.evaluate(environment);
			if (aVal instanceof Long && bVal instanceof Long) {
				return (Long)aVal + (Long)bVal;
			} else {
				return aVal.toString() + bVal.toString();
			}
		case OPERATOR_SUB:
			return (Long)a.evaluate(environment) - (Long)b.evaluate(environment);
		case OPERATOR_MUL:
			return (Long)a.evaluate(environment) * (Long)b.evaluate(environment);
		case OPERATOR_DIV:
			return (Long)a.evaluate(environment) / (Long)b.evaluate(environment);
		case OPERATOR_MOD:
			return (Long)a.evaluate(environment) % (Long)b.evaluate(environment);
		case OPERATOR_EQU:
			return a.evaluate(environment).equals(b.evaluate(environment));
		case OPERATOR_NEQ:
			return !a.evaluate(environment).equals(b.evaluate(environment));
		case OPERATOR_GEQ:
			return (Long)a.evaluate(environment) >= (Long)b.evaluate(environment);
		case OPERATOR_GTH:
			return (Long)a.evaluate(environment) > (Long)b.evaluate(environment);
		case OPERATOR_LEQ:
			return (Long)a.evaluate(environment) <= (Long)b.evaluate(environment);
		case OPERATOR_LTH:
			return (Long)a.evaluate(environment) < (Long)b.evaluate(environment);
		case OPERATOR_LAN:
			return (Boolean)a.evaluate(environment) && (Boolean)b.evaluate(environment);
		case OPERATOR_LOR:
			return (Boolean)a.evaluate(environment) || (Boolean)b.evaluate(environment);
		default:
			throw new IllegalArgumentException("Unknown operator " + operator);
		}
	}
	
	public String toString() {
		switch (operator) {
		case OPERATOR_ADD:
			return String.format("(%s + %s)", a, b);
		case OPERATOR_SUB:
			return String.format("(%s - %s)", a, b);
		case OPERATOR_MUL:
			return String.format("(%s * %s)", a, b);
		case OPERATOR_DIV:
			return String.format("(%s / %s)", a, b);
		case OPERATOR_MOD:
			return String.format("(%s % %s)", a, b);
		case OPERATOR_EQU:
			return String.format("(%s == %s)", a, b);
		case OPERATOR_NEQ:
			return String.format("(%s != %s)", a, b);
		case OPERATOR_GEQ:
			return String.format("(%s >= %s)", a, b);
		case OPERATOR_GTH:
			return String.format("(%s > %s)", a, b);
		case OPERATOR_LEQ:
			return String.format("(%s <= %s)", a, b);
		case OPERATOR_LTH:
			return String.format("(%s < %s)", a, b);
		case OPERATOR_LAN:
			return String.format("(%s && %s)", a, b);
		case OPERATOR_LOR:
			return String.format("(%s || %s)", a, b);
		default:
			throw new IllegalArgumentException("Unknown operator " + operator);
		}
	}
	
	public Collection<Instruction> compile() throws CompilationException {
		
		Collection<Instruction> instr = a.compile();
		instr.addAll(b.compile());
		
		if (operator != OPERATOR_ADD && !Number.class.isAssignableFrom(a.inferType()))
			throw new CompilationException("Invalid argument of operator. Must be a number");
		
		switch (operator) {
		case OPERATOR_ADD:
			if (Number.class.isAssignableFrom(inferType()))
				instr.add(new Instruction(Instruction.OP_IADD, (short)0));
			else if (inferType().equals(String.class))
				instr.add(new Instruction(Instruction.OP_SADD, (short)0));
			break;
		case OPERATOR_SUB:
			instr.add(new Instruction(Instruction.OP_ISUB, (short)0));
			break;
		case OPERATOR_MUL:
			instr.add(new Instruction(Instruction.OP_IMULT, (short)0));
			break;
		case OPERATOR_DIV:
			instr.add(new Instruction(Instruction.OP_IDIV, (short)0));
			break;
		case OPERATOR_MOD:
			instr.add(new Instruction(Instruction.OP_IMOD, (short)0));
			break;
		case OPERATOR_EQU:
			instr.add(new Instruction(Instruction.OP_IEQU, (short)0));
			break;
		case OPERATOR_NEQ:
			instr.add(new Instruction(Instruction.OP_INEQ, (short)0));
			break;
		case OPERATOR_GEQ:
			instr.add(new Instruction(Instruction.OP_IGEQ, (short)0));
			break;
		case OPERATOR_GTH:
			instr.add(new Instruction(Instruction.OP_IGTH, (short)0));
			break;
		case OPERATOR_LEQ:
			instr.add(new Instruction(Instruction.OP_ILEQ, (short)0));
			break;
		case OPERATOR_LTH:
			instr.add(new Instruction(Instruction.OP_ILTH, (short)0));
			break;
		case OPERATOR_LAN:
			instr.add(new Instruction(Instruction.OP_ILAN, (short)0));
			break;
		case OPERATOR_LOR:
			instr.add(new Instruction(Instruction.OP_ILOR, (short)0));
			break;
		default:
			throw new CompilationException("Unknown Operator");
		}
		
		return instr;
	}

	@Override
	public Class<?> inferType() throws CompilationException {
		Class<?> aType = a.inferType();
		Class<?> bType = b.inferType();
		if (!Type.matches(aType, bType))
			throw new CompilationException("Sub expressions must be of same type.");
		else if (aType.equals(String.class) || bType.equals(String.class))
			return String.class;
		else
			return aType;
	}

}
