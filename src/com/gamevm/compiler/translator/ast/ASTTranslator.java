package com.gamevm.compiler.translator.ast;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.gamevm.compiler.translator.CodeSection;
import com.gamevm.compiler.translator.TranslationException;
import com.gamevm.compiler.translator.Translator;
import com.gamevm.compiler.translator.TypedCode;
import com.gamevm.execution.ast.tree.Operator;

public abstract class ASTTranslator<I extends Instruction, D> extends Translator<ASTNode, I> {

	protected SymbolTable symbolTable;
	private Map<Instruction, ASTNode> debugInformation;

	private List<TranslationException> errors;

	//protected List<I> instructions;

	public ASTTranslator(SymbolTable symbolTable, boolean generateDebugInformation) {
		this.errors = new ArrayList<TranslationException>();
		this.symbolTable = symbolTable;
		//this.instructions = new ArrayList<I>(1024);
		if (generateDebugInformation)
			debugInformation = new HashMap<Instruction, ASTNode>();
	}

//	protected CodeSection mergeCodeSections(CodeSection a, CodeSection b) {
//		CodeSection first = (a.getStartIndex() > b.getStartIndex()) ? b : a;
//		CodeSection last = (first == a) ? b : a;
//		int lastLength = last.getLength();
//		List<I> lastList = instructions.subList(last.getStartIndex(), last.getEndIndex() + 1);
//		instructions.addAll(first.getEndIndex() + 1, lastList);
//		instructions.subList(last.getStartIndex() + lastLength, last.getEndIndex() + lastLength + 1).clear();
//		return new CodeSection(first.getStartIndex(), first.getEndIndex() + lastLength);
//	}

//	protected CodeSection castCheck(CodeSection c, Type codeType, Type resultType) {
//		if (codeType != resultType) {
//			int s = instructions.size();
//			generateCast(codeType, resultType, c);
//			CodeSection castSection = new CodeSection(s, instructions.size() - 1);
//			if (c.getEndIndex() < s-1)
//				return mergeCodeSections(c, castSection);
//			else
//				return new CodeSection(c.getStartIndex(), instructions.size() - 1);
//		}
//		return c;
//	}
	
	protected D castCheck(D orig, Type codeType, Type resultType) {
		if (codeType != resultType) {
			return generateCast(codeType, resultType, orig);
		}
		return orig;
	}

	// private CodeSection translateStatements(ASTNode n) throws
	// TranslationException {
	// return translateStatements(n, 0);
	// }

	private Collection<D> translateStatements(ASTNode n, int startIndex) throws TranslationException {
		Collection<D> result = new ArrayList<D>(n.getChildCount() - startIndex);
		for (int i = startIndex; i < n.getChildCount(); i++) {
			result.add(translate(n.getChildAt(i)));
		}
		return result;
	}

	private Collection<D> translateTypedExpressions(ASTNode n, int startIndex, Type... targetTypes) throws TranslationException {
		Collection<D> result = new ArrayList<D>(n.getChildCount() - startIndex);
		for (int i = 0; i < n.getChildCount() - startIndex; i++) {
			final ASTNode childNode = n.getChildAt(i);
			final Type targetType = targetTypes[i - startIndex];
			D descr = translateExpression(childNode);
			result.add(castCheck(descr, childNode.getValueType(), targetType));
		}
		return result;
	}

	protected void generateMethodInvocation(String methodName, ASTNode method, ClassSymbol symbol, CodeSection classExpression)
			throws TranslationException {
		
		Type[] parameterTypes = new Type[method.getChildCount() - 1];
		CodeSection[] parameterExpressions = new CodeSection[method.getChildCount() - 1];
		for (int i = 1; i < method.getChildCount(); i++) {
			parameterExpressions[i - 1] = translateExpression(method.getChildAt(i));
			parameterTypes[i - 1] = method.getChildAt(i).getValueType();
		}
		
		final int methodIndex;
		if (classExpression != null)
			methodIndex = symbol.getDeclaration().getMethod(methodName, parameterTypes);
		else
			methodIndex = symbol.getDeclaration().getMethod(true, methodName, parameterTypes);
		Method m = symbol.getDeclaration().getMethod(methodIndex);
		com.gamevm.compiler.assembly.Variable[] formalParameters = m.getParameters();
		for (int i = 0; i < parameterExpressions.length; i++) {
			parameterExpressions[i] = castCheck(parameterExpressions[i], parameterTypes[i], formalParameters[i].getType());
		}
		
		method.setValueType(m.getReturnType());

		if (methodName.equals("<init>")) {
			generateNewOperator(symbol.getIndex(), methodIndex, parameterExpressions);
		} else if (m.isStatic()) {
			generateStaticMethodInvocation(symbol.getIndex(), methodIndex, parameterExpressions);
		} else {

			if (classExpression == null && _method.isStatic())
				throw new TranslationException(String.format("Cannot invoke the instance method %s from a static method", methodName), method);

			generateMethodInvocation(symbol.getIndex(), methodIndex, classExpression, parameterExpressions);
		}
	}

	private TypedCode<D> translateExpression(ASTNode n) throws TranslationException {
		int startIndex = instructions.size();
		try {
			switch (n.getType()) {
			case ASTNode.TYPE_ASSIGNMENT:
				translateAssignment(n);
				break;
			case ASTNode.TYPE_METHOD_INVOCATION:
				translateMethodInvocation(n);
				break;
			case ASTNode.TYPE_OP_NEW:
				translateNewOperator(n);
				break;
			case ASTNode.TYPE_OP_NEW_ARRAY:
				translateNewArray(n);
				break;
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
				translateBinaryOp(n);
				break;
			case ASTNode.TYPE_OP_NEG:
			case ASTNode.TYPE_OP_LNEG:
				translateUnaryOp(n);
				break;
			case ASTNode.TYPE_LITERAL:
				translateLiteral(n);
				break;
			case ASTNode.TYPE_VARIABLE:
				translateVariable(n);
				break;
			case ASTNode.TYPE_QUALIFIED_ACCESS:
				translateQualifiedAccess(n);
				break;
			case ASTNode.TYPE_ARRAY_ACCESS:
				translateArrayAccess(n);
				break;
			default:
				throw new TranslationException("Unknown ASTNode " + ASTNode.strings[n.getType()], n);
			}
		} catch (Exception e1) {
			TranslationException t = new TranslationException(e1.getLocalizedMessage(), e1, n);
			//errors.add(t);
			throw t;
		}
		return new CodeSection(startIndex, instructions.size() - 1);
	}

	private CodeSection checkAssignmentCompatibility(Type leftType, Type rightType, CodeSection right, ASTNode node) throws TranslationException {
		if (!rightType.isAssignmentCompatibleTo(leftType)) {
			throw new TranslationException(String.format("Incompatible types %s and %s", leftType, rightType), node);
		} else {
			return castCheck(right, rightType, leftType);
		}
	}

	/* -------------- Code Generation Section -------------- */
	
	protected abstract D generateBlock(Collection<D> statements);

	protected abstract D generateLoop(D condition, D body);

	protected abstract D generateBranch(D condition, D body, D alternative);

	protected abstract D generateVariableInitialization(int variable, D initialization);

	protected abstract D generateAssignment(D left, D right);

	protected abstract D generateReturn(D expression);

	protected abstract D generateStaticMethodInvocation(int classIndex, int methodIndex, D[] parameters);

	protected abstract D generateMethodInvocation(int classIndex, int methodIndex, D classExpression, CodeSection[] parameters);

	protected abstract D generateNewOperator(int classIndex, int methodIndex, D[] parameters);

	protected abstract D generateNewArray(Type elementType, D[] dimensions);

	protected abstract D generateCast(Type origin, Type target, D expression);

	protected abstract D generateBinaryOperation(int type, Type operationType, D left, D right);

	protected abstract D generateUnaryOperation(int type, Type operationType, D operand);

	protected abstract D generateStringLiteral(String value);

	protected abstract D generateIntegerLiteral(int value);

	protected abstract D generateLongLiteral(long value);

	protected abstract D generateFloatLiteral(float value);

	protected abstract D generateDoubleLiteral(double value);

	protected abstract D generateCharLiteral(char value);

	protected abstract D generateBooleanLiteral(boolean value);

	protected abstract D generateVariableAccess(int variableIndex);

	protected abstract D generateStaticFieldAccess(int classIndex, int fieldIndex);

	protected abstract D generateFieldAccess(int classIndex, int fieldIndex, D classExpression);

	protected abstract D generateArrayAccess(D left, D index);

	/* ---------------- Translation Section ---------------- */

	protected TypedCode<D> translateBlock(ASTNode n) throws TranslationException {
		Collection<D> body = new ArrayList<D>();
		symbolTable.pushFrame(false);
		for (ASTNode c : n.getChildren())
			body.add(translate(c).getCodeDescriptor());
		symbolTable.popFrame();
		return new TypedCode<D>(generateBlock(body), null);
	}

	protected TypedCode<D> translateWhileLoop(ASTNode n) throws TranslationException {
		TypedCode<D> condition = translateExpression(n.getChildAt(0));
		D body = translate(n.getChildAt(1)).getCodeDescriptor();
		if (!(condition.getType() == Type.BOOLEAN))
			throw new TranslationException("The condition expression of a while loop must be of type boolean.", n);
		return new TypedCode<D>(generateLoop(condition.getCodeDescriptor(), body), null);
	}

	protected TypedCode<D> translateForLoop(ASTNode n) throws TranslationException {
		D init = translate(n.getChildAt(0)).getCodeDescriptor(); // init statement
		TypedCode<D> condition = translateExpression(n.getChildAt(1));
		Collection<D> body = translateStatements(n, 2);
		// the body contains the for body and the post body statements:
		return new TypedCode<D>(generateLoop(condition, body), null);
	}

	protected TypedCode<D> translateIf(ASTNode n) throws TranslationException {
		CodeSection condition = translateExpression(n.getChildAt(0));
		CodeSection body = translateExpression(n.getChildAt(1));
		CodeSection alternative = (n.getChildAt(2) != null) ? translate(n.getChildAt(2)) : null;
		generateBranch(condition, body, alternative);
	}

	protected TypedCode<D> translateVariableDeclaration(ASTNode n) throws TranslationException {
		Type type = (Type) n.getChildAt(0).getValue();
		String name = (String) n.getChildAt(1).getValue();
		int i = symbolTable.add(name, type);
		CodeSection initialization;
		if (n.getChildCount() > 2) {
			// this variable declaration contains a initialization
			initialization = translateExpression(n.getChildAt(2));
			initialization = checkAssignmentCompatibility(type, n.getChildAt(2).getValueType(), initialization, n);
		} else {
			initialization = translateExpression(new ASTNode(ASTNode.TYPE_LITERAL, n.getStartLine(), n.getStartPosition(), 0, type.getDefaultValue()));
		}
		generateVariableInitialization(i, initialization);
	}

	protected TypedCode<D> translateAssignment(ASTNode n) throws TranslationException {

		final ASTNode leftNode = n.getChildAt(0);
		final ASTNode rightNode = n.getChildAt(1);

		// Translation:
		CodeSection left = translateExpression(leftNode);
		CodeSection right = translateExpression(rightNode);

		// Checking:
		right = checkAssignmentCompatibility(leftNode.getValueType(), rightNode.getValueType(), right, n);
		if (!leftNode.isAddressable()) {
			throw new TranslationException(String.format("Left hand side of an assignment has to be an L-value"), n);
		}
		n.setValueType(n.getChildAt(0).getValueType());

		// Code Generation:
		generateAssignment(left, right);
	}

	protected TypedCode<D> translateReturn(ASTNode n) throws TranslationException {
		CodeSection expression = translateExpression(n.getChildAt(0));
		expression = checkAssignmentCompatibility(_method.getReturnType(), n.getChildAt(0).getValueType(), expression, n);
		generateReturn(expression);
	}

	protected TypedCode<D> translateMethodInvocation(ASTNode n) throws TranslationException {
		// if the control flow arrives here it must be a unqualified method call
		// otherwise this node would have been handled in the
		// TYPE_QUALIFIED_ACCESS case.
		String name = (String) n.getChildAt(0).getValue();
		generateMethodInvocation(name, n, symbolTable.getMainClass(), null);
	}

	protected TypedCode<D> translateNewOperator(ASTNode n) throws TranslationException {
		Type t = (Type) n.getChildAt(0).getValue();
		ClassSymbol classSymbol = symbolTable.getClass(t);
		generateMethodInvocation("<init>", n, classSymbol, null);
	}

	protected TypedCode<D> translateNewArray(ASTNode n) throws TranslationException {
		Type elementType = (Type) n.getChildAt(0).getValue();
		Type[] dimensionTypes = new Type[n.getChildCount() - 1];
		Arrays.fill(dimensionTypes, Type.INT);
		CodeSection[] dims = translateTypedExpressions(n, 1, dimensionTypes);
		n.setValueType(Type.getArrayType(elementType, n.getChildCount() - 1));
		generateNewArray(elementType, dims);
	}

	protected TypedCode<D> translateBinaryOp(ASTNode n) throws TranslationException {
		CodeSection a = translateExpression(n.getChildAt(0));
		CodeSection b = translateExpression(n.getChildAt(1));
		Type ta = n.getChildAt(0).getValueType();
		Type tb = n.getChildAt(1).getValueType();
		if (!Operator.typeIsValidForOperator(ta, n.getType())) {
			throw new TranslationException(String.format("Left operand of operator %s must be %s", Operator.getOperatorString(n.getType()),
					Operator.getDesiredTypeDescription(n.getType())), n.getChildAt(0));
		}
		if (!Operator.typeIsValidForOperator(tb, n.getType())) {
			throw new TranslationException(String.format("Right operand of operator %s must be %s", Operator.getOperatorString(n.getType()),
					Operator.getDesiredTypeDescription(n.getType())), n.getChildAt(1));
		}
		Type resultType = Operator.getResultType(n.getType(), ta, tb);
		a = castCheck(a, ta, resultType);
		b = castCheck(b, tb, resultType);
		n.setValueType(resultType);
		generateBinaryOperation(n.getType(), resultType, a, b);
	}

	protected TypedCode<D> translateUnaryOp(ASTNode n) throws TranslationException {
		CodeSection a = translateExpression(n.getChildAt(0));
		Type ta = n.getChildAt(0).getValueType();
		if (!Operator.typeIsValidForOperator(ta, n.getType())) {
			throw new TranslationException(String.format("Left operand of operator %s must be %s", Operator.getOperatorString(n.getType()),
					Operator.getDesiredTypeDescription(n.getType())), n.getChildAt(0));
		}
		n.setValueType(ta);
		generateUnaryOperation(n.getType(), ta, a);
	}

	protected TypedCode<D> translateLiteral(ASTNode n) {
		// value type is already set
		if (n.getValue() instanceof String) {
			generateStringLiteral((String) n.getValue());
		} else {
			if (n.getValueType() == Type.INT) {
				generateIntegerLiteral(((Integer) n.getValue()).intValue());
			} else if (n.getValueType() == Type.LONG) {
				generateLongLiteral(((Long) n.getValue()).longValue());
			} else if (n.getValueType() == Type.FLOAT) {
				generateFloatLiteral(((Float) n.getValue()).floatValue());
			} else if (n.getValueType() == Type.DOUBLE) {
				generateDoubleLiteral(((Double) n.getValue()).doubleValue());
			} else if (n.getValueType() == Type.CHAR) {
				generateCharLiteral(((Character) n.getValue()).charValue());
			} else if (n.getValueType() == Type.BOOLEAN) {
				generateBooleanLiteral(((Boolean) n.getValue()).booleanValue());
			}
		}
	}

	protected TypedCode<D> translateVariable(ASTNode n) throws TranslationException {
		// if control flow arrives here it must be a local variable otherwise
		// this node would have been handled in the TYPE_QUALIFIED_ACCESS case.
		String name = (String) n.getValue();
		int vindex = symbolTable.getIndex(name);
		if (vindex >= 0) {
			// a local variable:
			n.setValueType(symbolTable.getSymbol(name).getType());
			generateVariableAccess(vindex);
		} else {
			ClassDeclaration d = symbolTable.getMainClass().getDeclaration();
			vindex = d.getField(name);
			if (vindex >= 0) {
				// a field:
				Field f = d.getField(vindex);
				n.setValueType(f.getType());

				if (f.isStatic()) {
					generateStaticFieldAccess(symbolTable.getMainClass().getIndex(), vindex);
				} else {
					if (_method.isStatic())
						throw new TranslationException(String.format("Cannot access the instance field %s from a static method", name), n);
					generateFieldAccess(-1, vindex, null);
				}
			} else {
				// possibly a name (e.g. part of a qualified class identifier):
				if (Type.isType(name)) {
					n.setValueType(Type.getType(name));
				} else {
					n.setValueType(null);
				}
				_qualifiedName = new QualifiedNameExpression(name);
			}
		}
	}

	protected TypedCode<D> translateQualifiedAccess(ASTNode n) throws TranslationException {
		CodeSection left = translateExpression(n.getChildAt(0));
		Type leftType = n.getChildAt(0).getValueType();

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
				n.setValueType(Type.getType(nameStr));
			} else {
				n.setValueType(null);
			}
		} else if (_qualifiedName != null) {
			// The name on the left must be a QualifiedNameExpression
			// that contains a valid class name:
			left = null;
		}

		ClassSymbol leftClass = symbolTable.getClass(leftType);
		ASTNode rightNode = n.getChildAt(1);
		if (rightNode.getType() == ASTNode.TYPE_METHOD_INVOCATION) {
			String methodName = (String) rightNode.getChildAt(0).getValue();
			generateMethodInvocation(methodName, rightNode, leftClass, left);
			n.setValueType(rightNode.getValueType());
		} else {
			final int fieldIndex;
			if (left != null)
				fieldIndex = leftClass.getDeclaration().getField((String) n.getChildAt(1).getValue());
			else
				fieldIndex = leftClass.getDeclaration().getField(false, (String) n.getChildAt(1).getValue());
			if (fieldIndex < 0)
				throw new TranslationException("Unknown field " + n.getChildAt(1).getValue(), n);
			Field f = leftClass.getDeclaration().getField(fieldIndex);
			if (!f.isStatic()) {
				generateFieldAccess(leftClass.getIndex(), fieldIndex, left);
			} else {
				generateStaticFieldAccess(leftClass.getIndex(), fieldIndex);
			}
			n.setValueType(f.getType());

		}
	}

	protected TypedCode<D> translateArrayAccess(ASTNode n) throws TranslationException {
		CodeSection left = translateExpression(n.getChildAt(0));
		CodeSection index = translateExpression(n.getChildAt(1));
		// TODO: type checking
		n.setValueType(n.getChildAt(0).getValueType().getElementType());
		generateArrayAccess(left, index);
	}

	private TypedCode<D> translate(ASTNode n) throws TranslationException {
		int startIndex = instructions.size();
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
		int endIndex = instructions.size() - 1;
		return new CodeSection(startIndex, endIndex);
	}

	private Method _method;
	private QualifiedNameExpression _qualifiedName;

	@Override
	protected List<I> generateCode(Method m, List<ASTNode> src) throws TranslationException {
		instructions.clear();
		if (src.size() > 0) {
			ASTNode block = src.iterator().next();
			_method = m;
			if (block.getType() == ASTNode.TYPE_BLOCK) {
				symbolTable.pushFrame(true);
				for (com.gamevm.compiler.assembly.Variable v : _method.getParameters()) {
					symbolTable.add(v.getName(), v.getType());
				}
				for (ASTNode n : block.getChildren()) {
					translate(n);
				}
				symbolTable.popFrame();
			} else {
				symbolTable.pushFrame(true);
				for (ASTNode n : src) {
					translateExpression(n);
				}
				symbolTable.popFrame();
			}
		}
		List<I> result = new ArrayList<I>(instructions.size());
		result.addAll(instructions);
		return result;
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
	public List<TranslationException> getErrors() {
		return errors;
	}
}
