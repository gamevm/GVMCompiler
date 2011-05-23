package com.gamevm.compiler.translator.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.Field;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.compiler.assembly.code.Code;
import com.gamevm.compiler.assembly.code.TreeCode;
import com.gamevm.compiler.parser.ASTNode;
import com.gamevm.compiler.parser.LiteralObject;
import com.gamevm.compiler.translator.TranslationException;
import com.gamevm.compiler.translator.Translator;
import com.gamevm.execution.ast.tree.Operator;

public abstract class ASTTranslator<C extends Code> extends Translator<TreeCode<ASTNode>, C> {

	protected static final int ALL = -1;
	
	protected SymbolTable symbolTable;
	private List<TranslationException> errors;

	private Method _method;
	private QualifiedNameExpression _qualifiedName;

	public ASTTranslator(SymbolTable symbolTable) {
		this.errors = new ArrayList<TranslationException>();
		this.symbolTable = symbolTable;
	}

	@Override
	public List<TranslationException> getErrors() {
		return errors;
	}

	@Override
	public C translate(Method m, TreeCode<ASTNode> src) throws TranslationException {
		ASTNode block = src.getRoot();
		if (block != null) {
			_method = m;
			symbolTable.pushFrame();
			for (com.gamevm.compiler.assembly.Variable v : _method.getParameters()) {
				symbolTable.add(v.getName(), v.getType());
			}
			for (ASTNode n : block.getChildren()) {
				translate(n);
			}
			generateBlock(ALL);
			symbolTable.popFrame();
		}
		return getCode();
	}

	protected abstract C getCode();

	private void translateTypedExpressions(ASTNode n, int startIndex, Type... targetTypes) throws TranslationException {
		for (int i = 0; i < n.getChildCount() - startIndex; i++) {
			final ASTNode childNode = n.getChildAt(i);
			final Type targetType = targetTypes[i - startIndex];
			Type expType = translateExpression(childNode);
			checkAssignmentCompatibility(targetType, expType, childNode);
		}
	}

	protected Type translateMethodInvocation(String methodName, ASTNode method, ClassSymbol symbol, boolean staticCall,
			boolean implicitThis) throws TranslationException {

		Type[] parameterTypes = new Type[method.getChildCount() - 1];
		for (int i = 1; i < method.getChildCount(); i++) {
			parameterTypes[i - 1] = translateExpression(method.getChildAt(i));
			generateParameter();
		}

		final int methodIndex;
		if (staticCall)
			methodIndex = symbol.getDeclaration().getMethod(true, methodName, parameterTypes);
		else
			methodIndex = symbol.getDeclaration().getMethod(methodName, parameterTypes);

		Method m = symbol.getDeclaration().getMethod(methodIndex);
		com.gamevm.compiler.assembly.Variable[] formalParameters = m.getParameters();
		for (int i = 0; i < formalParameters.length; i++) {
			checkAssignmentCompatibility(formalParameters.length - i - 1, formalParameters[i].getType(), parameterTypes[i], method);
		}

		if (methodName.equals("<init>")) {
			generateNewOperator(symbol.getIndex(), methodIndex);
		} else if (m.isStatic()) {
			generateStaticMethodInvocation(symbol.getIndex(), methodIndex);
		} else {

			if (symbol == symbolTable.getMainClass() && _method.isStatic())
				throw new TranslationException(String.format(
						"Cannot invoke the instance method %s from a static method", methodName), method);

			generateMethodInvocation(symbol.getIndex(), methodIndex, implicitThis);
		}

		return m.getReturnType();
	}

	private void checkAssignmentCompatibility(int stackDepth, Type leftType, Type rightType, ASTNode node) throws TranslationException {
		if (!rightType.isAssignmentCompatibleTo(leftType)) {
			throw new TranslationException(String.format("Incompatible types %s and %s", leftType, rightType), node);
		} else {
			if (leftType != rightType)
				generateCast(stackDepth, rightType, leftType);
		}
	}
	
	private void checkAssignmentCompatibility(Type leftType, Type rightType, ASTNode node) throws TranslationException {
		checkAssignmentCompatibility(0, leftType, rightType, node);
	}

	/* -------------- Code Generation Section -------------- */

	protected abstract void generateBlock(int size);

	/**
	 * This method is called, after a boolean condition expression and a
	 * statement have been generated.
	 */
	protected abstract void generateLoop();

	protected abstract void generateBranch(boolean hasAlternative);

	protected abstract void generateVariableInitialization(int varIndex);

	protected abstract void generateReturn();

	protected abstract void generateAssignment();

	protected abstract void generateParameter();

	protected abstract void generateStaticMethodInvocation(int classIndex, int methodIndex);

	protected abstract void generateMethodInvocation(int classIndex, int methodIndex, boolean implicitThis);

	protected abstract void generateNewOperator(int classIndex, int methodIndex);

	protected abstract void generateNewArray(Type elementType, int dimension);

	protected abstract void generateBinaryOperation(int type, Type operationType);

	protected abstract void generateUnaryOperation(int type, Type operationType);

	protected abstract void generateStringLiteral(String value);

	protected abstract void generateIntegerLiteral(int value);

	protected abstract void generateLongLiteral(long value);

	protected abstract void generateFloatLiteral(float value);

	protected abstract void generateDoubleLiteral(double value);

	protected abstract void generateCharLiteral(char value);

	protected abstract void generateBooleanLiteral(boolean value);

	protected abstract void generateVariableAccess(int variableIndex);

	protected abstract void generateStaticFieldAccess(int classIndex, int fieldIndex);

	protected abstract void generateFieldAccess(int classIndex, int fieldIndex, boolean implicitThis);

	protected abstract void generateArrayAccess();
	
	protected abstract void generateCast(int stackDepth, Type sourceType, Type targetType);

	/* ---------------- Translation Section ---------------- */

	protected void translateBlock(ASTNode n) throws TranslationException {
		symbolTable.pushFrame();
		for (ASTNode c : n.getChildren())
			translate(c);
		generateBlock(n.getChildCount());
		symbolTable.popFrame();
	}

	protected void translateWhileLoop(ASTNode n) throws TranslationException {
		Type conditionType = translateExpression(n.getChildAt(0));
		translate(n.getChildAt(1));

		if (!(conditionType == Type.BOOLEAN))
			throw new TranslationException("The condition expression of a while loop must be of type boolean.", n);

		generateLoop();
	}

	protected void translateForLoop(ASTNode n) throws TranslationException {
		// create a fake block node to unify post-loop actions and loop body:
		ASTNode fakeNode = new ASTNode(ASTNode.TYPE_BLOCK);
		int startIndex = 2;
		if (n.getChildAt(2).getType() == ASTNode.TYPE_BLOCK) {
			for (ASTNode blockNode : n.getChildAt(2).getChildren()) {
				fakeNode.addNode(blockNode);
			}
			startIndex = 3;
		}
		for (int i = startIndex; i < n.getChildCount(); i++) {
			fakeNode.addNode(n.getChildAt(i));
		}

		translate(n.getChildAt(0)); // init statement
		Type conditionType = translateExpression(n.getChildAt(1));
		translate(fakeNode);

		if (!(conditionType == Type.BOOLEAN))
			throw new TranslationException("The condition expression of a while loop must be of type boolean.", n);

		generateLoop();
	}

	protected void translateIf(ASTNode n) throws TranslationException {
		Type conditionType = translateExpression(n.getChildAt(0));
		translate(n.getChildAt(1));
		if (n.getChildAt(2) != null)
			translate(n.getChildAt(2));

		if (!(conditionType == Type.BOOLEAN))
			throw new TranslationException("The condition expression of a while loop must be of type boolean.", n);

		generateBranch(n.getChildAt(2) != null);
	}

	protected void translateVariableDeclaration(ASTNode n) throws TranslationException {
		Type type = (Type) n.getChildAt(0).getValue();
		String name = (String) n.getChildAt(1).getValue();
		int i = symbolTable.add(name, type);
		if (n.getChildCount() > 2) {
			// this variable declaration contains a initialization
			Type initializationType = translateExpression(n.getChildAt(2));
			checkAssignmentCompatibility(type, initializationType, n);
		} else {
			translateExpression(new ASTNode(ASTNode.TYPE_LITERAL, n.getStartLine(), n.getStartPosition(), 0,
					type.getDefaultValue()));
		}
		generateVariableInitialization(i);
	}

	protected Type translateAssignment(ASTNode n) throws TranslationException {
		Type ltype = translateExpression(n.getChildAt(0));
		Type rtype = translateExpression(n.getChildAt(1));

		checkAssignmentCompatibility(ltype, rtype, n);
		if (!n.getChildAt(0).isAddressable()) {
			throw new TranslationException(String.format("Left hand side of an assignment has to be an L-value"), n);
		}

		generateAssignment();

		return ltype;
	}

	protected void translateReturn(ASTNode n) throws TranslationException {
		Type type = translateExpression(n.getChildAt(0));

		checkAssignmentCompatibility(_method.getReturnType(), type, n);

		generateReturn();
	}

	protected Type translateMethodInvocation(ASTNode n) throws TranslationException {
		// if the control flow arrives here it must be a unqualified method call
		// otherwise this node would have been handled in the
		// TYPE_QUALIFIED_ACCESS case.
		String name = (String) n.getChildAt(0).getValue();
		return translateMethodInvocation(name, n, symbolTable.getMainClass(), false, true);
	}

	protected Type translateNewOperator(ASTNode n) throws TranslationException {
		Type t = (Type) n.getChildAt(0).getValue();
		ClassSymbol classSymbol = symbolTable.getClass(t);
		return translateMethodInvocation("<init>", n, classSymbol, false, false);
	}

	protected Type translateNewArray(ASTNode n) throws TranslationException {
		Type elementType = (Type) n.getChildAt(0).getValue();
		Type[] dimensionTypes = new Type[n.getChildCount() - 1];
		Arrays.fill(dimensionTypes, Type.INT);

		translateTypedExpressions(n, 1, dimensionTypes);

		generateNewArray(elementType, dimensionTypes.length);

		return Type.getArrayType(elementType, n.getChildCount() - 1);
	}

	protected Type translateBinaryOp(ASTNode n) throws TranslationException {
		Type ta = translateExpression(n.getChildAt(0));
		Type tb = translateExpression(n.getChildAt(1));

		if (!Operator.typeIsValidForOperator(ta, n.getType())) {
			throw new TranslationException(String.format("Left operand of operator %s must be %s",
					Operator.getOperatorString(n.getType()), Operator.getDesiredTypeDescription(n.getType())),
					n.getChildAt(0));
		}
		if (!Operator.typeIsValidForOperator(tb, n.getType())) {
			throw new TranslationException(String.format("Right operand of operator %s must be %s",
					Operator.getOperatorString(n.getType()), Operator.getDesiredTypeDescription(n.getType())),
					n.getChildAt(1));
		}
		Type operationType = Type.getCommonType(ta, tb);

		generateBinaryOperation(n.getType(), operationType);

		return Operator.getResultType(n.getType(), ta, tb);
	}

	protected Type translateUnaryOp(ASTNode n) throws TranslationException {
		Type ta = translateExpression(n.getChildAt(0));

		if (!Operator.typeIsValidForOperator(ta, n.getType())) {
			throw new TranslationException(String.format("Left operand of operator %s must be %s",
					Operator.getOperatorString(n.getType()), Operator.getDesiredTypeDescription(n.getType())),
					n.getChildAt(0));
		}

		generateUnaryOperation(n.getType(), ta);

		return ta;
	}

	protected Type translateLiteral(ASTNode n) {
		LiteralObject l = (LiteralObject) n.getValue();

		if (l.getValue() instanceof String) {
			generateStringLiteral((String) l.getValue());
		} else {
			if (l.getType() == Type.INT) {
				generateIntegerLiteral(((Integer) l.getValue()).intValue());
			} else if (l.getType() == Type.LONG) {
				generateLongLiteral(((Long) l.getValue()).longValue());
			} else if (l.getType() == Type.FLOAT) {
				generateFloatLiteral(((Float) l.getValue()).floatValue());
			} else if (l.getType() == Type.DOUBLE) {
				generateDoubleLiteral(((Double) l.getValue()).doubleValue());
			} else if (l.getType() == Type.CHAR) {
				generateCharLiteral(((Character) l.getValue()).charValue());
			} else if (l.getType() == Type.BOOLEAN) {
				generateBooleanLiteral(((Boolean) l.getValue()).booleanValue());
			}
		}

		return l.getType();
	}

	protected Type translateVariable(ASTNode n) throws TranslationException {
		// if control flow arrives here it must be a local variable otherwise
		// this node would have been handled in the TYPE_QUALIFIED_ACCESS case.
		String name = (String) n.getValue();
		int vindex = symbolTable.getIndex(name);
		if (vindex >= 0) {
			// a local variable:
			generateVariableAccess(vindex);
			return symbolTable.getSymbol(name).getType();
		} else {
			ClassDeclaration d = symbolTable.getMainClass().getDeclaration();
			vindex = d.getField(name);
			if (vindex >= 0) {
				// a field:
				Field f = d.getField(vindex);

				if (f.isStatic()) {
					generateStaticFieldAccess(symbolTable.getMainClass().getIndex(), vindex);
				} else {
					if (_method.isStatic())
						throw new TranslationException(String.format(
								"Cannot access the instance field %s from a static method", name), n);
					generateFieldAccess(symbolTable.getMainClass().getIndex(), vindex, true);
				}
				return f.getType();
			} else {
				// possibly a name (e.g. part of a qualified class identifier):
				_qualifiedName = new QualifiedNameExpression(name);
				if (Type.isType(name)) {
					return Type.getType(name);
				} else {
					return null;
				}
			}
		}
	}

	protected Type translateQualifiedAccess(ASTNode n) throws TranslationException {
		Type leftType = translateExpression(n.getChildAt(0));

		boolean isStatic = false;
		if (leftType == null) {
			// The name on the left must be a QualifiedNameExpression
			// that contains only a package name. This allows only a
			// package or class name on the right side:

			if (n.getChildAt(1).getType() == ASTNode.TYPE_METHOD_INVOCATION)
				throw new TranslationException("Method invocation on package name is not allowed.", n);

			String rightName = (String) n.getChildAt(1).getValue();
			_qualifiedName.appendName(rightName);
			String nameStr = _qualifiedName.evaluate();
			if (Type.isType(nameStr)) {
				return Type.getType(nameStr);
			} else {
				return null;
			}
		} else if (_qualifiedName != null) {
			// The name on the left must be a QualifiedNameExpression
			// that contains a valid class name:
			isStatic = true;
		}

		ClassSymbol leftClass = symbolTable.getClass(leftType);
		ASTNode rightNode = n.getChildAt(1);
		if (rightNode.getType() == ASTNode.TYPE_METHOD_INVOCATION) {
			String methodName = (String) rightNode.getChildAt(0).getValue();
			return translateMethodInvocation(methodName, rightNode, leftClass, isStatic, false);
		} else {
			final int fieldIndex;
			if (isStatic)
				fieldIndex = leftClass.getDeclaration().getField((String) n.getChildAt(1).getValue());
			else
				fieldIndex = leftClass.getDeclaration().getField(false, (String) n.getChildAt(1).getValue());
			if (fieldIndex < 0)
				throw new TranslationException("Unknown field '" + n.getChildAt(1).getValue() + "'", n);
			Field f = leftClass.getDeclaration().getField(fieldIndex);
			if (!f.isStatic()) {
				generateFieldAccess(leftClass.getIndex(), fieldIndex, false);
			} else {
				generateStaticFieldAccess(leftClass.getIndex(), fieldIndex);
			}
			return f.getType();

		}
	}

	protected Type translateArrayAccess(ASTNode n) throws TranslationException {
		Type leftType = translateExpression(n.getChildAt(0));
		Type indexType = translateExpression(n.getChildAt(1));

		checkAssignmentCompatibility(Type.INT, indexType, n);

		generateArrayAccess();

		return leftType.getElementType();
	}

	private Type translateExpression(ASTNode n) throws TranslationException {
		try {
			switch (n.getType()) {
			case ASTNode.TYPE_ASSIGNMENT:
				return translateAssignment(n);
			case ASTNode.TYPE_METHOD_INVOCATION:
				return translateMethodInvocation(n);
			case ASTNode.TYPE_OP_NEW:
				return translateNewOperator(n);
			case ASTNode.TYPE_OP_NEW_ARRAY:
				return translateNewArray(n);
			case ASTNode.TYPE_OP_LAND:
			case ASTNode.TYPE_OP_LOR:
			case ASTNode.TYPE_OP_NEQ:
			case ASTNode.TYPE_OP_EQU:
			case ASTNode.TYPE_OP_GTH:
			case ASTNode.TYPE_OP_LTH:
			case ASTNode.TYPE_OP_GEQ:
			case ASTNode.TYPE_OP_LEQ:
			case ASTNode.TYPE_OP_PLUS:
			case ASTNode.TYPE_OP_MINUS:
			case ASTNode.TYPE_OP_MULT:
			case ASTNode.TYPE_OP_DIV:
			case ASTNode.TYPE_OP_MOD:
				return translateBinaryOp(n);
			case ASTNode.TYPE_OP_NEG:
			case ASTNode.TYPE_OP_LNEG:
				return translateUnaryOp(n);
			case ASTNode.TYPE_LITERAL:
				return translateLiteral(n);
			case ASTNode.TYPE_VARIABLE:
				return translateVariable(n);
			case ASTNode.TYPE_QUALIFIED_ACCESS:
				return translateQualifiedAccess(n);
			case ASTNode.TYPE_ARRAY_ACCESS:
				return translateArrayAccess(n);
			default:
				throw new TranslationException("Unknown ASTNode " + ASTNode.strings[n.getType()], n);
			}
		} catch (Exception e1) {
			TranslationException t = new TranslationException(e1.getLocalizedMessage(), e1, n);
			throw t;
		}
	}

	private void translate(ASTNode n) throws TranslationException {
		try {
			switch (n.getType()) {
			case ASTNode.TYPE_BLOCK:
				translateBlock(n);
				break;
			case ASTNode.TYPE_WHILE_LOOP:
				translateWhileLoop(n);
				break;
			case ASTNode.TYPE_FOR_LOOP:
				translateForLoop(n);
				break;
			case ASTNode.TYPE_IF:
				translateIf(n);
				break;
			case ASTNode.TYPE_VAR_DECL:
				translateVariableDeclaration(n);
				break;
			case ASTNode.TYPE_ASSIGNMENT:
				translateAssignment(n);
				break;
			case ASTNode.TYPE_RETURN:
				translateReturn(n);
				break;
			case ASTNode.TYPE_METHOD_INVOCATION:
				translateMethodInvocation(n);
				break;
			case ASTNode.TYPE_QUALIFIED_ACCESS:
				if (n.getChildAt(1).getType() != ASTNode.TYPE_METHOD_INVOCATION)
					throw new TranslationException(String.format("Unknown method %s", n.getChildAt(1).getValue()), n);
				translateQualifiedAccess(n);
				break;
			default:
				throw new TranslationException("Unknown ASTNode " + ASTNode.strings[n.getType()], n);
			}
		} catch (Exception e1) {
			errors.add(new TranslationException(e1.getLocalizedMessage(), e1, n));
		}
	}

}
