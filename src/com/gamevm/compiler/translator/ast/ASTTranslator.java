package com.gamevm.compiler.translator.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.Field;
import com.gamevm.compiler.assembly.Instruction;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.compiler.parser.ASTNode;
import com.gamevm.compiler.translator.TranslationException;
import com.gamevm.compiler.translator.Translator;
import com.gamevm.execution.ast.ClassInstance;
import com.gamevm.execution.ast.builtin.ArrayInstance;
import com.gamevm.execution.ast.builtin.StringInstance;
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
import com.gamevm.execution.ast.tree.OpNew;
import com.gamevm.execution.ast.tree.OpNewArray;
import com.gamevm.execution.ast.tree.ReturnStatement;
import com.gamevm.execution.ast.tree.Statement;
import com.gamevm.execution.ast.tree.StaticFieldAccess;
import com.gamevm.execution.ast.tree.StaticMethodInvocation;
import com.gamevm.execution.ast.tree.Variable;
import com.gamevm.execution.ast.tree.VariableDelcaration;
import com.gamevm.execution.ast.tree.WhileStatement;

public class ASTTranslator extends Translator<ASTNode, Statement> {

	private SymbolTable symbolTable;
	private Map<Instruction, ASTNode> debugInformation;
	
	private List<TranslationException> errors;

	public ASTTranslator(SymbolTable symbolTable, boolean generateDebugInformation) {
		this.errors = new ArrayList<TranslationException>();
		this.symbolTable = symbolTable;
		if (generateDebugInformation)
			debugInformation = new HashMap<Instruction, ASTNode>();
	}

	private <T> T addDebugInformation(ASTNode n, Instruction i) {
		if (debugInformation != null)
			debugInformation.put(i, n);
		return (T) i;
	}

	private <T extends Statement> List<T> translateStatements(ASTNode n) throws TranslationException {
		return translateStatements(n, 0);
	}

	private <T extends Statement> List<T> translateStatements(ASTNode n, int startIndex)
			throws TranslationException {
		List<T> body = new ArrayList<T>();
		for (int i = startIndex; i < n.getChildCount(); i++) {
			body.add((T) translate(n.getChildAt(i)));
		}
		return body;
	}

	private <T extends Expression<?>> List<T> translateExpressions(ASTNode n, int startIndex)
			throws TranslationException {
		List<T> exprs = new ArrayList<T>();
		for (int i = startIndex; i < n.getChildCount(); i++) {
			exprs.add((T) translateExpression(n.getChildAt(i)));
		}
		return exprs;
	}

	private Expression<Boolean> translateBooleanOp(int type, ASTNode n) throws TranslationException {
		Expression<Boolean> a = translateExpression(n.getChildAt(0));
		Expression<Boolean> b = translateExpression(n.getChildAt(1));
		Type ta = n.getChildAt(0).getValueType();
		Type tb = n.getChildAt(1).getValueType();
		if (ta != Type.BOOLEAN || tb != Type.BOOLEAN)
			throw new TranslationException("Boolean operator only accepts boolean arguments", n);
		n.setValueType(Type.BOOLEAN);
		return (type == ASTNode.TYPE_OP_LAND) ? new OpLogicalAnd(a, b) : new OpLogicalOr(a, b);
	}

	private Expression<Boolean> translateEqualityOp(int type, ASTNode n) throws TranslationException {
		Expression<Object> a = translateExpression(n.getChildAt(0));
		Expression<Object> b = translateExpression(n.getChildAt(1));
		n.setValueType(Type.BOOLEAN);
		return (type == ASTNode.TYPE_OP_EQU) ? new OpComparisonEquals(a, b) : new OpComparisonUnequals(a, b);
	}

	@SuppressWarnings("unchecked")
	private Expression<Boolean> translateComparisonOp(int type, ASTNode n) throws TranslationException {
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
			return new OpComparisonInteger((Expression<Integer>) a, (Expression<Integer>) b, type);
		case Type.ORDINAL_INT:
			if (ta != t)
				a = new Cast<Integer>(a, Type.INT);
			if (tb != t)
				b = new Cast<Integer>(b, Type.INT);
			return new OpComparisonInteger((Expression<Integer>) a, (Expression<Integer>) b, type);
		case Type.ORDINAL_LONG:
			if (ta != t)
				a = new Cast<Long>(a, Type.LONG);
			if (tb != t)
				b = new Cast<Long>(b, Type.LONG);
			return new OpComparisonLong((Expression<Long>) a, (Expression<Long>) b, type);
		case Type.ORDINAL_FLOAT:
			if (ta != t)
				a = new Cast<Float>(a, Type.FLOAT);
			if (tb != t)
				b = new Cast<Float>(b, Type.FLOAT);
			return new OpComparisonFloat((Expression<Float>) a, (Expression<Float>) b, type);
		case Type.ORDINAL_DOUBLE:
			if (ta != t)
				a = new Cast<Double>(a, Type.DOUBLE);
			if (tb != t)
				b = new Cast<Double>(b, Type.DOUBLE);
			return new OpComparisonDouble((Expression<Double>) a, (Expression<Double>) b, type);
		default:
			throw new TranslationException("Comparison of type " + t + " is not supported", n);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> Expression<T> translateArithOp(int type, ASTNode n) throws TranslationException {
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
			return (Expression<T>) new OpArithInteger((Expression<Integer>) a, (Expression<Integer>) b, type);
		case Type.ORDINAL_INT:
			if (ta != t)
				a = new Cast<Integer>(a, Type.INT);
			if (tb != t)
				b = new Cast<Integer>(b, Type.INT);
			n.setValueType(t);
			return (Expression<T>) new OpArithInteger((Expression<Integer>) a, (Expression<Integer>) b, type);
		case Type.ORDINAL_LONG:
			if (ta != t)
				a = new Cast<Long>(a, Type.LONG);
			if (tb != t)
				b = new Cast<Long>(b, Type.LONG);
			n.setValueType(t);
			return (Expression<T>) new OpArithLong((Expression<Long>) a, (Expression<Long>) b, type);
		case Type.ORDINAL_FLOAT:
			if (ta != t)
				a = new Cast<Float>(a, Type.FLOAT);
			if (tb != t)
				b = new Cast<Float>(b, Type.FLOAT);
			n.setValueType(t);
			return (Expression<T>) new OpArithFloat((Expression<Float>) a, (Expression<Float>) b, type);
		case Type.ORDINAL_DOUBLE:
			if (ta != t)
				a = new Cast<Double>(a, Type.DOUBLE);
			if (tb != t)
				b = new Cast<Double>(b, Type.DOUBLE);
			n.setValueType(t);
			return (Expression<T>) new OpArithDouble((Expression<Double>) a, (Expression<Double>) b, type);
		default:
			throw new TranslationException("Arithmetic on type " + t + " is not supported", n);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> Expression<T> translateNegOp(ASTNode n) throws TranslationException {
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
			throw new TranslationException("Negation on type " + t + " is not supported", n);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> AbstractMethodInvocation<T> getMethod(ASTNode method, ClassSymbol symbol, Expression<?> left)
			throws TranslationException {
		String name = (String) method.getChildAt(0).getValue();
		List<Expression<?>> parameters = translateExpressions(method, 1);
		Type[] parameterTypes = new Type[parameters.size()];
		for (int i = 0; i < parameterTypes.length; i++) {
			parameterTypes[i] = method.getChildAt(i + 1).getValueType();
		}
		final int methodIndex;
		if (left != null)
			methodIndex = symbol.getDeclaration().getMethod(name, parameterTypes);
		else
			methodIndex = symbol.getDeclaration().getMethod(true, name, parameterTypes);
		Method m = symbol.getDeclaration().getMethod(methodIndex);
		com.gamevm.compiler.assembly.Variable[] formalParameters = m.getParameters();
		
		method.setValueType(m.getReturnType());
		
		for (int i = 1; i < method.getChildCount(); i++) {
			if (method.getChildAt(i).getValueType() != formalParameters[i-1].getType()) {
				// insert cast:
				parameters.set(i-1, Cast.getCast(parameters.get(i-1), formalParameters[i-1].getType()));
			}
		}

		if (m.isStatic()) {
			return new StaticMethodInvocation<T>(symbol.getIndex(), methodIndex, parameters, symbol.getDeclaration());
		} else {

			if (left == null && _method.isStatic())
				throw new TranslationException(String.format(
						"Cannot invoke the instance method %s from a static method", name), method);

			return new MethodInvocation<T>(symbol.getIndex(), (Expression<ClassInstance>) left, methodIndex,
					parameters, symbol.getDeclaration());
		}
	}

	private OpNew getConstructor(ASTNode newOperator) throws TranslationException {
		Type t = (Type) newOperator.getChildAt(0).getValue();

		ClassSymbol csymbol = symbolTable.getClass(t);
		Collection<Expression<?>> parameters = translateExpressions(newOperator, 1);
		Type[] parameterTypes = new Type[parameters.size()];
		for (int i = 0; i < parameterTypes.length; i++) {
			parameterTypes[i] = newOperator.getChildAt(i + 1).getValueType();
		}
		int constructorIndex = csymbol.getDeclaration().getMethod("<init>", parameterTypes);

		newOperator.setValueType(t);

		return new OpNew(csymbol.getIndex(), constructorIndex, parameters, csymbol.getDeclaration());
	}

	@SuppressWarnings("unchecked")
	private <T> Expression<T> translateExpression(ASTNode n) throws TranslationException {
		final String s;
		final Expression<?> a;
		final Expression<?> b;
		String op;
		try {
			switch (n.getType()) {
			case ASTNode.TYPE_ASSIGNMENT:
				Expression<T> left = translateExpression(n.getChildAt(0));
				Expression<T> right = translateExpression(n.getChildAt(1));
				right = (Expression<T>)checkAssignmentCompatibility(n.getChildAt(0).getValueType(), n.getChildAt(1).getValueType(), n, right);
				if (left instanceof NotAddressable) {
					throw new TranslationException(String.format("%s is not a valid L-value", left.toString(0)), n);
				}
				n.setValueType(n.getChildAt(0).getValueType());
				return addDebugInformation(n, new Assignment<T>(left, right));
			case ASTNode.TYPE_METHOD_INVOCATION:
				// if control flow arrives here it must be a unqualified method
				// call
				// otherwise this node would have been handled in the
				// TYPE_QUALIFIED_ACCESS
				// case.
				return getMethod(n, symbolTable.getMainClass(), null);
			case ASTNode.TYPE_OP_NEW:
				return addDebugInformation(n, getConstructor(n));
			case ASTNode.TYPE_OP_NEW_ARRAY:
				Type elementType = (Type) n.getChildAt(0).getValue();
				Collection<Expression<Integer>> dims = translateExpressions(n, 1);
				n.setValueType(Type.getArrayType(elementType, n.getChildCount() - 1));
				return addDebugInformation(n, new OpNewArray(elementType.getDefaultValue(), dims, elementType));
			case ASTNode.TYPE_OP_LAND:
			case ASTNode.TYPE_OP_LOR:
				return addDebugInformation(n, translateBooleanOp(n.getType(), n));
			case ASTNode.TYPE_OP_NEQ:
			case ASTNode.TYPE_OP_EQU:
				return addDebugInformation(n, translateEqualityOp(n.getType(), n));
			case ASTNode.TYPE_OP_GTH:
			case ASTNode.TYPE_OP_LTH:
			case ASTNode.TYPE_OP_GEQ:
			case ASTNode.TYPE_OP_LEQ:
				return addDebugInformation(n, translateComparisonOp(n.getType(), n));
			case ASTNode.TYPE_OP_PLUS:
			case ASTNode.TYPE_OP_MINUS:
			case ASTNode.TYPE_OP_MULT:
			case ASTNode.TYPE_OP_DIV:
			case ASTNode.TYPE_OP_MOD:
				return addDebugInformation(n, translateArithOp(n.getType(), n));
			case ASTNode.TYPE_OP_NEG:
				return addDebugInformation(n, translateNegOp(n));
			case ASTNode.TYPE_OP_LNEG:
				Expression<Boolean> bex = translateExpression(n.getChildAt(0));
				if (n.getChildAt(0).getValueType() != Type.BOOLEAN)
					throw new TranslationException("Operator ! is only applicable to boolean", n);
				n.setValueType(Type.BOOLEAN);
				return addDebugInformation(n, new OpLNeg(bex));
			case ASTNode.TYPE_LITERAL:
				// value type is already set
				if (n.getValue() instanceof String) {
					return addDebugInformation(n, new Literal<T>((T)new StringInstance((String)n.getValue())));
				} else {
					return addDebugInformation(n, new Literal<T>((T) n.getValue()));
				}
			case ASTNode.TYPE_VARIABLE:
				// if control flow arrives here it must be a local variable
				// otherwise this node would have been handled in the
				// TYPE_QUALIFIED_ACCESS
				// case.
				s = (String) n.getValue();
				int vindex = symbolTable.getIndex(s);
				if (vindex < 0) {
					ClassDeclaration d = symbolTable.getMainClass().getDeclaration();
					vindex = d.getField(s);
					if (vindex < 0) {
						if (Type.isType(s)) {
							n.setValueType(Type.getType(s));
						} else {
							n.setValueType(null);
						}
						return (Expression<T>) new QualifiedNameExpression(s);
					}
					Field f = d.getField(vindex);
					n.setValueType(f.getType());

					if (f.isStatic()) {
						return new StaticFieldAccess<T>(symbolTable.getMainClass().getIndex(), vindex, null, f.getName());
					} else {
						if (_method.isStatic())
							throw new TranslationException(String.format(
									"Cannot access the instance field %s from a static method", s), n);
						return addDebugInformation(n, new FieldAccess<T>(d, null, vindex));
					}

				}
				n.setValueType(symbolTable.getSymbol(s).getType());
				return addDebugInformation(n, new Variable<T>(vindex, s));
			case ASTNode.TYPE_QUALIFIED_ACCESS:
				left = translateExpression(n.getChildAt(0));
				Type leftType = n.getChildAt(0).getValueType();

				if (leftType == null) {
					// The name on the left must be a QualifiedNameExpression
					// that contains only a package name. This allows only a
					// package or class name on the right side:

					if (n.getChildAt(1).getType() == ASTNode.TYPE_METHOD_INVOCATION)
						throw new TranslationException("Method invocation on package name is not allowed.", n);

					String rightName = (String) n.getChildAt(1).getValue();
					QualifiedNameExpression name = (QualifiedNameExpression) left;
					name.appendName(rightName);
					String nameStr = name.evaluate();
					if (Type.isType(nameStr)) {
						n.setValueType(Type.getType(nameStr));
					} else {
						n.setValueType(null);
					}
					return (Expression<T>) name;

				} else if (left instanceof QualifiedNameExpression) {
					// The name on the left must be a QualifiedNameExpression
					// that contains a valid class name:
					left = null;
				}

				ClassSymbol leftClass = symbolTable.getClass(leftType);
				if (n.getChildAt(1).getType() == ASTNode.TYPE_METHOD_INVOCATION) {
					AbstractMethodInvocation<T> m = getMethod(n.getChildAt(1), leftClass, left);
					n.setValueType(n.getChildAt(1).getValueType());
					return addDebugInformation(n, m);
				} else {
					final int fieldIndex;
					if (left != null)
						fieldIndex = leftClass.getDeclaration().getField((String) n.getChildAt(1).getValue());
					else
						fieldIndex = leftClass.getDeclaration().getField(false, (String) n.getChildAt(1).getValue());
					if (fieldIndex < 0)
						throw new TranslationException("Unknown field " + n.getChildAt(1).getValue(), n);
					Field f = leftClass.getDeclaration().getField(fieldIndex);
					n.setValueType(f.getType());
					if (!f.isStatic()) {
						return addDebugInformation(n, new FieldAccess<T>(leftClass.getDeclaration(),
								(Expression<ClassInstance>) left, fieldIndex));
					} else {
						return addDebugInformation(n,
								new StaticFieldAccess<T>(leftClass.getIndex(), fieldIndex, leftClass.getDeclaration().getName(), f.getName()));
					}

				}
			case ASTNode.TYPE_ARRAY_ACCESS:
				Expression<?> arrLeft = translateExpression(n.getChildAt(0));
				Expression<Integer> arrIndex = translateExpression(n.getChildAt(1));
				// TODO: type checking
				n.setValueType(n.getChildAt(0).getValueType().getElementType());
				return addDebugInformation(n, new OpArrayAccess<T>((Expression<ArrayInstance>) arrLeft, arrIndex));
			default:
				throw new TranslationException("Unknown ASTNode " + ASTNode.strings[n.getType()], n);
			}
		} catch (Exception e1) {
			TranslationException t = new TranslationException(e1.getLocalizedMessage(), e1, n);
			errors.add(t);
			throw t;
		}
	}

	private Expression<?> checkAssignmentCompatibility(Type leftType, Type rightType, ASTNode node, Expression<?> right) throws TranslationException {
		if (!rightType.isAssignmentCompatibleTo(leftType)) {
			throw new TranslationException(String.format("Incompatible types %s and %s", leftType, rightType), node);
		} else {
			if (leftType != rightType) {
				return Cast.getCast(right, leftType);
			}
		}
		return right;
	}

	@SuppressWarnings("unchecked")
	private Statement translate(ASTNode n) throws TranslationException {
		final Expression<Boolean> c;
		Expression<Object> e;
		final Block b;
		final String s;
		final Type t;
		try {
			switch (n.getType()) {
			case ASTNode.TYPE_BLOCK:
				symbolTable.pushFrame(false);
				Collection<Statement> body = translateStatements(n);
				symbolTable.popFrame();
				return addDebugInformation(n, new Block(body));
			case ASTNode.TYPE_WHILE_LOOP:
				c = translateExpression(n.getChildAt(0));
				return addDebugInformation(n, new WhileStatement(c, translate(n.getChildAt(1))));
			case ASTNode.TYPE_FOR_LOOP:
				Statement initStatement = translate(n.getChildAt(0));
				c = translateExpression(n.getChildAt(1));
				return addDebugInformation(n, new ForStatement(initStatement, c,
						translateStatements(n, 3), translate(n.getChildAt(2))));
			case ASTNode.TYPE_IF:
				c = translateExpression(n.getChildAt(0));
				return addDebugInformation(n,
						new IfStatement(c, translate(n.getChildAt(1)), translate(n.getChildAt(2))));
			case ASTNode.TYPE_VAR_DECL:
				t = (Type) n.getChildAt(0).getValue();
				s = (String) n.getChildAt(1).getValue();
				int i = symbolTable.add(s, t);
				if (n.getChildCount() > 2) {
					e = translateExpression(n.getChildAt(2));
					e = (Expression<Object>)checkAssignmentCompatibility(t, n.getChildAt(2).getValueType(), n, e);
					return addDebugInformation(n, new VariableDelcaration<Object>(i, t, s, e));
				} else {
					return addDebugInformation(n, new VariableDelcaration<Object>(i, t, s, null));
				}
			case ASTNode.TYPE_ASSIGNMENT:
				return new ExpressionStatement(translateExpression(n));
			case ASTNode.TYPE_RETURN:
				e = translateExpression(n.getChildAt(0));
				e = (Expression<Object>)checkAssignmentCompatibility(_method.getReturnType(), n.getChildAt(0).getValueType(), n, e);
				return addDebugInformation(n, new ReturnStatement<Object>(e));
			case ASTNode.TYPE_METHOD_INVOCATION:
				return addDebugInformation(n, new ExpressionStatement(translateExpression(n)));
			case ASTNode.TYPE_QUALIFIED_ACCESS:
				if (n.getChildAt(1).getType() != ASTNode.TYPE_METHOD_INVOCATION)
					throw new TranslationException(String.format("Unknown method %s", n.getChildAt(1).getValue()), n);
				return addDebugInformation(n, new ExpressionStatement(translateExpression(n)));
			default:
				throw new TranslationException("Unknown ASTNode " + ASTNode.strings[n.getType()], n);
			}
		} catch (Exception e1) {
			errors.add(new TranslationException(e1.getLocalizedMessage(), e1, n));
			return null;
		}
	}

	private Method _method;

	@Override
	protected List<Statement> generateCode(Method m, List<ASTNode> src) throws TranslationException {
		if (src.size() > 0) {
			ASTNode block = src.iterator().next();
			_method = m;
			if (block.getType() == ASTNode.TYPE_BLOCK) {
				List<Statement> result = new ArrayList<Statement>(block.getChildCount());
				symbolTable.pushFrame(true);
				for (com.gamevm.compiler.assembly.Variable v : _method.getParameters()) {
					symbolTable.add(v.getName(), v.getType());
				}
				for (ASTNode n : block.getChildren()) {
					result.add((Statement) addDebugInformation(n, translate(n)));
				}
				symbolTable.popFrame();
				return result;
			} else {
				List<Statement> result = new ArrayList<Statement>(src.size());
				symbolTable.pushFrame(true);
				for (ASTNode n : src) {
					result.add((Statement) addDebugInformation(n, new ExpressionStatement(translateExpression(n))));
				}
				symbolTable.popFrame();
				return result;
			}
		} else {
			return new ArrayList<Statement>();
		}
	}

	@Override
	protected Map<Instruction, ASTNode> getDebugInformation() {
		return debugInformation;
	}

	@Override
	public Class<ASTNode> getSourceInstructionType() {
		return ASTNode.class;
	}

	@Override
	public Class<Statement> getTargetInstructionType() {
		return Statement.class;
	}

	@Override
	public List<TranslationException> getErrors() {
		return errors;
	}
}
