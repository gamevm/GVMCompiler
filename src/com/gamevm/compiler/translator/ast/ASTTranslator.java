package com.gamevm.compiler.translator.ast;

import java.util.ArrayList;
import java.util.Collection;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.Field;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.compiler.assembly.Translator;
import com.gamevm.compiler.assembly.Type;
import com.gamevm.compiler.parser.ASTNode;
import com.gamevm.compiler.translator.TranslationException;
import com.gamevm.execution.ast.ClassInstance;
import com.gamevm.execution.ast.tree.AbstractMethodInvocation;
import com.gamevm.execution.ast.tree.Assignment;
import com.gamevm.execution.ast.tree.Block;
import com.gamevm.execution.ast.tree.Cast;
import com.gamevm.execution.ast.tree.Expression;
import com.gamevm.execution.ast.tree.ExpressionStatement;
import com.gamevm.execution.ast.tree.FieldAccess;
import com.gamevm.execution.ast.tree.ForStatement;
import com.gamevm.execution.ast.tree.IfStatement;
import com.gamevm.execution.ast.tree.Literal;
import com.gamevm.execution.ast.tree.MethodInvocation;
import com.gamevm.execution.ast.tree.NotAddressable;
import com.gamevm.execution.ast.tree.OpArithDouble;
import com.gamevm.execution.ast.tree.OpArithFloat;
import com.gamevm.execution.ast.tree.OpArithInteger;
import com.gamevm.execution.ast.tree.OpArithLong;
import com.gamevm.execution.ast.tree.OpArrayAccess;
import com.gamevm.execution.ast.tree.OpComparisonDouble;
import com.gamevm.execution.ast.tree.OpComparisonEquals;
import com.gamevm.execution.ast.tree.OpComparisonFloat;
import com.gamevm.execution.ast.tree.OpComparisonInteger;
import com.gamevm.execution.ast.tree.OpComparisonLong;
import com.gamevm.execution.ast.tree.OpComparisonUnequals;
import com.gamevm.execution.ast.tree.OpLNeg;
import com.gamevm.execution.ast.tree.OpLogicalAnd;
import com.gamevm.execution.ast.tree.OpLogicalOr;
import com.gamevm.execution.ast.tree.OpNegDouble;
import com.gamevm.execution.ast.tree.OpNegFloat;
import com.gamevm.execution.ast.tree.OpNegInteger;
import com.gamevm.execution.ast.tree.OpNegLong;
import com.gamevm.execution.ast.tree.ReturnStatement;
import com.gamevm.execution.ast.tree.Statement;
import com.gamevm.execution.ast.tree.StaticFieldAccess;
import com.gamevm.execution.ast.tree.StaticMethodInvocation;
import com.gamevm.execution.ast.tree.Variable;
import com.gamevm.execution.ast.tree.VariableDelcaration;
import com.gamevm.execution.ast.tree.WhileStatement;

public class ASTTranslator extends Translator<ASTNode, Statement> {

	private SymbolTable symbolTable;

	public ASTTranslator(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	private <T extends Statement> Collection<T> translateStatements(ASTNode n)
			throws TranslationException {
		return translateStatements(n, 0);
	}

	private <T extends Statement> Collection<T> translateStatements(ASTNode n,
			int startIndex) throws TranslationException {
		Collection<T> body = new ArrayList<T>();
		for (int i = startIndex; i < n.getChildCount(); i++) {
			body.add((T) translate(n.getChildAt(i)));
		}
		return body;
	}

	private <T extends Expression<?>> Collection<T> translateExpressions(
			ASTNode n, int startIndex) throws TranslationException {
		Collection<T> exprs = new ArrayList<T>();
		for (int i = startIndex; i < n.getChildCount(); i++) {
			exprs.add((T) translateExpression(n.getChildAt(i)));
		}
		return exprs;
	}

	private Expression<Boolean> translateBooleanOp(int type, ASTNode n)
			throws TranslationException {
		Expression<Boolean> a = translateExpression(n.getChildAt(0));
		Expression<Boolean> b = translateExpression(n.getChildAt(1));
		Type ta = n.getChildAt(0).getValueType();
		Type tb = n.getChildAt(1).getValueType();
		if (ta.ordinal() != Type.ORDINAL_BOOLEAN
				|| tb.ordinal() != Type.ORDINAL_BOOLEAN)
			throw new TranslationException(
					"Boolean operator only accepts boolean arguments", n);
		n.setValueType(Type.BOOLEAN);
		return (type == ASTNode.TYPE_OP_LAND) ? new OpLogicalAnd(a, b)
				: new OpLogicalOr(a, b);
	}

	private Expression<Boolean> translateEqualityOp(int type, ASTNode n)
			throws TranslationException {
		Expression<Object> a = translateExpression(n.getChildAt(0));
		Expression<Object> b = translateExpression(n.getChildAt(1));
		n.setValueType(Type.BOOLEAN);
		return (type == ASTNode.TYPE_OP_EQU) ? new OpComparisonEquals(a, b)
				: new OpComparisonUnequals(a, b);
	}

	@SuppressWarnings("unchecked")
	private Expression<Boolean> translateComparisonOp(int type, ASTNode n)
			throws TranslationException {
		Expression<?> a = translateExpression(n.getChildAt(0));
		Expression<?> b = translateExpression(n.getChildAt(1));
		Type ta = n.getChildAt(0).getValueType();
		Type tb = n.getChildAt(1).getValueType();
		Type t = Type.getCommonType(ta, tb);
		n.setValueType(Type.BOOLEAN);
		switch (t.ordinal()) {
		case Type.ORDINAL_BYTE:
		case Type.ORDINAL_SHORT:
			a = new Cast<Integer>(a, Type.INT);
			b = new Cast<Integer>(b, Type.INT);
			return new OpComparisonInteger((Expression<Integer>) a,
					(Expression<Integer>) b, type);
		case Type.ORDINAL_INT:
			if (ta != t)
				a = new Cast<Integer>(a, Type.INT);
			if (tb != t)
				b = new Cast<Integer>(b, Type.INT);
			return new OpComparisonInteger((Expression<Integer>) a,
					(Expression<Integer>) b, type);
		case Type.ORDINAL_LONG:
			if (ta != t)
				a = new Cast<Long>(a, Type.LONG);
			if (tb != t)
				b = new Cast<Long>(b, Type.LONG);
			return new OpComparisonLong((Expression<Long>) a,
					(Expression<Long>) b, type);
		case Type.ORDINAL_FLOAT:
			if (ta != t)
				a = new Cast<Float>(a, Type.FLOAT);
			if (tb != t)
				b = new Cast<Float>(b, Type.FLOAT);
			return new OpComparisonFloat((Expression<Float>) a,
					(Expression<Float>) b, type);
		case Type.ORDINAL_DOUBLE:
			if (ta != t)
				a = new Cast<Double>(a, Type.DOUBLE);
			if (tb != t)
				b = new Cast<Double>(b, Type.DOUBLE);
			return new OpComparisonDouble((Expression<Double>) a,
					(Expression<Double>) b, type);
		default:
			throw new TranslationException("Comparison of type " + t
					+ " is not supported", n);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> Expression<T> translateArithOp(int type, ASTNode n)
			throws TranslationException {
		Expression<?> a = translateExpression(n.getChildAt(0));
		Expression<?> b = translateExpression(n.getChildAt(1));
		Type ta = n.getChildAt(0).getValueType();
		Type tb = n.getChildAt(1).getValueType();
		Type t = Type.getCommonType(ta, tb);

		switch (t.ordinal()) {
		case Type.ORDINAL_BYTE:
		case Type.ORDINAL_SHORT:
			a = new Cast<Integer>(a, Type.INT);
			b = new Cast<Integer>(b, Type.INT);
			n.setValueType(Type.INT);
			return (Expression<T>) new OpArithInteger((Expression<Integer>) a,
					(Expression<Integer>) b, type);
		case Type.ORDINAL_INT:
			if (ta != t)
				a = new Cast<Integer>(a, Type.INT);
			if (tb != t)
				b = new Cast<Integer>(b, Type.INT);
			n.setValueType(t);
			return (Expression<T>) new OpArithInteger((Expression<Integer>) a,
					(Expression<Integer>) b, type);
		case Type.ORDINAL_LONG:
			if (ta != t)
				a = new Cast<Long>(a, Type.LONG);
			if (tb != t)
				b = new Cast<Long>(b, Type.LONG);
			n.setValueType(t);
			return (Expression<T>) new OpArithLong((Expression<Long>) a,
					(Expression<Long>) b, type);
		case Type.ORDINAL_FLOAT:
			if (ta != t)
				a = new Cast<Float>(a, Type.FLOAT);
			if (tb != t)
				b = new Cast<Float>(b, Type.FLOAT);
			n.setValueType(t);
			return (Expression<T>) new OpArithFloat((Expression<Float>) a,
					(Expression<Float>) b, type);
		case Type.ORDINAL_DOUBLE:
			if (ta != t)
				a = new Cast<Double>(a, Type.DOUBLE);
			if (tb != t)
				b = new Cast<Double>(b, Type.DOUBLE);
			n.setValueType(t);
			return (Expression<T>) new OpArithDouble((Expression<Double>) a,
					(Expression<Double>) b, type);
		default:
			throw new TranslationException("Arithmetic on type " + t
					+ " is not supported", n);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> Expression<T> translateNegOp(ASTNode n)
			throws TranslationException {
		Expression<?> a = translateExpression(n.getChildAt(0));
		Type t = n.getChildAt(0).getValueType();
		switch (t.ordinal()) {
		case Type.ORDINAL_BYTE:
		case Type.ORDINAL_SHORT:
			a = new Cast<Integer>(a, Type.INT);
			n.setValueType(Type.INT);
			return (Expression<T>) new OpNegInteger((Expression<Integer>) a);
		case Type.ORDINAL_INT:
			n.setValueType(t);
			return (Expression<T>) new OpNegInteger((Expression<Integer>) a);
		case Type.ORDINAL_LONG:
			n.setValueType(t);
			return (Expression<T>) new OpNegLong((Expression<Long>) a);
		case Type.ORDINAL_FLOAT:
			n.setValueType(t);
			return (Expression<T>) new OpNegFloat((Expression<Float>) a);
		case Type.ORDINAL_DOUBLE:
			n.setValueType(t);
			return (Expression<T>) new OpNegDouble((Expression<Double>) a);
		default:
			throw new TranslationException("Negation on type " + t
					+ " is not supported", n);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> AbstractMethodInvocation<T, ?> getMethod(ASTNode method,
			ClassSymbol symbol, Expression<?> left) throws TranslationException {
		String name = (String) method.getChildAt(0).getValue();
		Collection<Expression<?>> parameters = translateExpressions(method, 1);
		Type[] parameterTypes = new Type[parameters.size()];
		for (int i = 0; i < parameterTypes.length; i++) {
			parameterTypes[i] = method.getChildAt(i + 1).getValueType();
		}
		int methodIndex = symbol.getDeclaration().getMethod(name,
				parameterTypes);
		Method m = symbol.getDeclaration().getMethod(methodIndex);

		method.setValueType(m.getReturnType());

		if (m.isStatic()) {
			return new StaticMethodInvocation<T>(symbol.getIndex(),
					methodIndex, parameters);
		} else {
			return new MethodInvocation<T>(symbol.getIndex(),
					(Expression<ClassInstance>) left, methodIndex, parameters);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> Expression<T> translateExpression(ASTNode n)
			throws TranslationException {
		final String s;
		final Expression<?> a;
		final Expression<?> b;
		String op;
		try {
			switch (n.getType()) {
			case ASTNode.TYPE_ASSIGNMENT:
				Expression<T> left = translateExpression(n.getChildAt(0));
				Expression<T> right = translateExpression(n.getChildAt(1));
				checkAssignmentCompatibility(n.getChildAt(0).getValueType(), n
						.getChildAt(1).getValueType(), n);
				if (left instanceof NotAddressable) {
					throw new TranslationException(String.format(
							"%s is not a valid L-value", left.toString(0)), n);
				}
				n.setValueType(n.getChildAt(0).getValueType());
				return new Assignment<T>(left, right);
			case ASTNode.TYPE_METHOD_INVOCATION:
				// if control flow arrives here it must be a unqualified method
				// call
				// otherwise this node would have been handled in the
				// TYPE_QUALIFIED_ACCESS
				// case.
				return getMethod(n, symbolTable.getMainClass(), null);
			case ASTNode.TYPE_OP_LAND:
			case ASTNode.TYPE_OP_LOR:
				return (Expression<T>) translateBooleanOp(n.getType(), n);
			case ASTNode.TYPE_OP_NEQ:
			case ASTNode.TYPE_OP_EQU:
				return (Expression<T>) translateEqualityOp(n.getType(), n);
			case ASTNode.TYPE_OP_GTH:
			case ASTNode.TYPE_OP_LTH:
			case ASTNode.TYPE_OP_GEQ:
			case ASTNode.TYPE_OP_LEQ:
				return (Expression<T>) translateComparisonOp(n.getType(), n);
			case ASTNode.TYPE_OP_PLUS:
			case ASTNode.TYPE_OP_MINUS:
			case ASTNode.TYPE_OP_MULT:
			case ASTNode.TYPE_OP_DIV:
			case ASTNode.TYPE_OP_MOD:
				return translateArithOp(n.getType(), n);
			case ASTNode.TYPE_OP_NEG:
				return translateNegOp(n);
			case ASTNode.TYPE_OP_LNEG:
				Expression<Boolean> bex = translateExpression(n.getChildAt(0));
				if (n.getChildAt(0).getValueType() != Type.BOOLEAN)
					throw new TranslationException(
							"Operator ! is only applicable to boolean", n);
				n.setValueType(Type.BOOLEAN);
				return (Expression<T>) new OpLNeg(bex);
			case ASTNode.TYPE_LITERAL:
				// value type is already set
				return new Literal<T>((T) n.getValue());
			case ASTNode.TYPE_VARIABLE:
				// if control flow arrives here it must be a local variable
				// otherwise this node would have been handled in the
				// TYPE_QUALIFIED_ACCESS
				// case.
				s = (String) n.getValue();
				int vindex = symbolTable.getIndex(s);
				if (vindex < 0) {
					ClassDeclaration d = symbolTable.getMainClass()
							.getDeclaration();
					vindex = d.getField(s);
					if (vindex < 0)
						throw new TranslationException("Unknown variable " + s,
								n);
					n.setValueType(d.getField(vindex).getType());
					return new FieldAccess<T>(d, null, vindex);
				}
				n.setValueType(symbolTable.getSymbol(s).getType());
				return new Variable<T>(vindex);
			case ASTNode.TYPE_QUALIFIED_ACCESS:
				left = translateExpression(n.getChildAt(0));
				Type leftType = n.getChildAt(0).getValueType();
				ClassSymbol leftClass = symbolTable
						.getClass(leftType.getName());
				if (n.getChildAt(0).getType() == ASTNode.TYPE_METHOD_INVOCATION) {
					return getMethod(n, leftClass, left);
				} else {
					int fieldIndex = leftClass.getDeclaration().getField(
							(String) n.getChildAt(0).getValue());
					Field f = leftClass.getDeclaration().getField(fieldIndex);
					n.setValueType(f.getType());
					if (f.isStatic()) {
						return new FieldAccess<T>(leftClass.getDeclaration(),
								(Expression<ClassInstance>) left, fieldIndex);
					} else {
						return new StaticFieldAccess<T>(leftClass.getIndex(),
								fieldIndex);
					}
				}
			case ASTNode.TYPE_ARRAY_ACCESS:
				Expression<?> arrLeft = translateExpression(n.getChildAt(0));
				Expression<Integer> arrIndex = translateExpression(n
						.getChildAt(1));
				// TODO: type checking
				n.setValueType(Type.getElementType(n.getChildAt(0)
						.getValueType()));
				return new OpArrayAccess<T>((Expression<T[]>) arrLeft, arrIndex);
			default:
				throw new TranslationException("Unknown ASTNode "
						+ ASTNode.strings[n.getType()], n);
			}
		} catch (Exception e1) {
			throw new TranslationException(n.toString() + "\n" + e1.getLocalizedMessage(), e1, n);
		}
	}

	private void checkAssignmentCompatibility(Type left, Type right,
			ASTNode node) throws TranslationException {
		if (!right.isAssignmentCompatible(left)) {
			throw new TranslationException(String.format(
					"Incompatible types %s and %s", left, right), node);
		}
	}

	@SuppressWarnings("unchecked")
	private Statement translate(ASTNode n) throws TranslationException {
		final Expression<Boolean> c;
		final Expression<Object> e;
		final Block b;
		final String s;
		final Type t;
		try {
			switch (n.getType()) {
			case ASTNode.TYPE_BLOCK:
				symbolTable.pushFrame();
				Collection<Statement> body = translateStatements(n);
				symbolTable.popFrame();
				return new Block(body);
			case ASTNode.TYPE_WHILE_LOOP:
				c = translateExpression(n.getChildAt(0));
				b = (Block) translate(n.getChildAt(1));
				return new WhileStatement(c, b);
			case ASTNode.TYPE_FOR_LOOP:
				c = translateExpression(n.getChildAt(1));
				return new ForStatement(translate(n.getChildAt(0)), c,
						translateStatements(n.getChildAt(2)),
						translateStatements(n, 3));
			case ASTNode.TYPE_IF:
				Collection<IfStatement> elses = translateStatements(n, 2);
				c = translateExpression(n.getChildAt(0));
				b = (Block) translate(n.getChildAt(1));
				return new IfStatement(c, b, elses);
			case ASTNode.TYPE_VAR_DECL:
				t = (Type) n.getChildAt(0).getValue();
				s = (String) n.getChildAt(1).getValue();
				int i = symbolTable.add(s, t);
				if (n.getChildCount() > 2) {
					e = translateExpression(n.getChildAt(2));
					checkAssignmentCompatibility(t, n.getChildAt(2)
							.getValueType(), n);
					return new VariableDelcaration<Object>(i, t, e);
				} else {
					return new VariableDelcaration<Object>(i, t, null);
				}
			case ASTNode.TYPE_ASSIGNMENT:
				return new ExpressionStatement(translateExpression(n));
			case ASTNode.TYPE_RETURN:
				e = translateExpression(n.getChildAt(0));
				checkAssignmentCompatibility(_method.getReturnType(), n
						.getChildAt(0).getValueType(), n);
				return new ReturnStatement<Object>(e);
			case ASTNode.TYPE_METHOD_INVOCATION:
				return new ExpressionStatement(translateExpression(n));
			default:
				throw new TranslationException("Unknown ASTNode "
						+ ASTNode.strings[n.getType()], n);
			}
		} catch (Exception e1) {
			throw new TranslationException(n.toString() + "\n" + e1.getLocalizedMessage(), e1, n);
		}
	}

	private Method _method;

	@Override
	protected Statement[] generateCode(Method m, ASTNode... src)
			throws TranslationException {
		if (src.length != 1)
			throw new IllegalArgumentException(
					"Expecting only one block ast node");

		ASTNode block = src[0];
		_method = m;
		if (block.getType() == ASTNode.TYPE_BLOCK) {
			Statement[] result = new Statement[block.getChildCount()];
			symbolTable.pushFrame();
			for (com.gamevm.compiler.assembly.Variable v : _method
					.getParameters()) {
				symbolTable.add(v.getName(), v.getType());
			}
			for (int i = 0; i < result.length; i++) {
				result[i] = translate(block.getChildAt(i));
			}
			symbolTable.popFrame();
			return result;
		} else {
			Statement[] result = new Statement[src.length];
			symbolTable.pushFrame();
			for (int i = 0; i < result.length; i++) {
				result[i] = new ExpressionStatement(translateExpression(src[i]));
			}
			symbolTable.popFrame();
			return result;
		}
	}
}
