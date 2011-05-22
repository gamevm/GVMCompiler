package com.gamevm.compiler.translator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.assembly.code.TreeCode;
import com.gamevm.compiler.translator.ast.ASTTranslator;
import com.gamevm.compiler.translator.ast.SymbolTable;

public abstract class TreeTranslator<T> extends ASTTranslator<TreeCode<T>> {

	private Stack<T> stack;
	private int parameterCount;

	public TreeTranslator(SymbolTable symbolTable) {
		super(symbolTable);
		stack = new Stack<T>();
		parameterCount = 0;
	}

	protected abstract T newLoop(T condition, T body);

	protected abstract T newBranch(T condition, T body, T alternative);

	protected abstract T newVariableInitialization(int varIndex, T initialization);

	protected abstract T newReturn(T expression);

	protected abstract T newAssignment(T lexpr, T rexpr);

	protected abstract T newStaticMethodInvocation(int classIndex, int methodIndex, List<T> parameters);

	protected abstract T newMethodInvocation(int classIndex, int methodIndex, T classExpression, List<T> parameters);

	protected abstract T newNewOperator(int classIndex, int methodIndex, List<T> parameters);

	protected abstract T newNewArray(Type elementType, List<T> sizeExpressions);

	protected abstract T newUnaryOperator(int type, Type operationType, T operand);

	protected abstract T newBinaryOperator(int type, Type operationType, T left, T right);

	protected abstract T newLiteral(Object value, Type type);

	protected abstract T newVariableAccess(int index);

	protected abstract T newStaticFieldAccess(int classIndex, int fieldIndex);

	protected abstract T newFieldAccess(int classIndex, int fieldIndex, T classExpression);

	protected abstract T newArrayAccess(T left, T index);

	protected abstract TreeCode<T> getCode(T root);

	@Override
	protected TreeCode<T> getCode() {
		return getCode(stack.pop());
	}

	@Override
	protected void generateLoop() {
		final T body = stack.pop();
		final T condition = stack.pop();
		stack.push(newLoop(condition, body));
	}

	@Override
	protected void generateBranch(boolean hasAlternative) {
		final T alternative = (hasAlternative) ? stack.pop() : null;
		final T body = stack.pop();
		final T condition = stack.pop();
		stack.push(newBranch(condition, body, alternative));
	}

	@Override
	protected void generateVariableInitialization(int varIndex) {
		final T initialization = stack.pop();
		stack.push(newVariableInitialization(varIndex, initialization));
	}

	@Override
	protected void generateReturn() {
		final T expression = stack.pop();
		stack.push(newReturn(expression));
	}

	@Override
	protected void generateAssignment() {
		final T lexpr = stack.pop();
		final T rexpr = stack.pop();
		stack.push(newAssignment(lexpr, rexpr));
	}

	@Override
	protected void generateParameter() {
		parameterCount++;
	}

	@Override
	protected void generateStaticMethodInvocation(int classIndex, int methodIndex) {
		stack.push(newStaticMethodInvocation(classIndex, methodIndex, getParameters()));

	}

	@Override
	protected void generateMethodInvocation(int classIndex, int methodIndex, boolean implicitThis) {
		final T classExpression = (implicitThis) ? null : stack.pop();
		stack.push(newMethodInvocation(classIndex, methodIndex, classExpression, getParameters()));
	}

	@Override
	protected void generateNewOperator(int classIndex, int methodIndex) {
		stack.push(newNewOperator(classIndex, methodIndex, getParameters()));
	}

	@Override
	protected void generateNewArray(Type elementType, int dimension) {
		stack.push(newNewArray(elementType, pop(dimension)));
	}

	@Override
	protected void generateBinaryOperation(int type, Type operationType) {
		final T left = stack.pop();
		final T right = stack.pop();
		stack.push(newBinaryOperator(type, operationType, left, right));
	}

	@Override
	protected void generateUnaryOperation(int type, Type operationType) {
		final T operand = stack.pop();
		stack.push(newUnaryOperator(type, operationType, operand));
	}

	@Override
	protected void generateStringLiteral(String value) {
		stack.push(newLiteral(value, Type.STRING));
	}

	@Override
	protected void generateIntegerLiteral(int value) {
		stack.push(newLiteral(value, Type.INT));
	}

	@Override
	protected void generateLongLiteral(long value) {
		stack.push(newLiteral(value, Type.LONG));
	}

	@Override
	protected void generateFloatLiteral(float value) {
		stack.push(newLiteral(value, Type.FLOAT));
	}

	@Override
	protected void generateDoubleLiteral(double value) {
		stack.push(newLiteral(value, Type.DOUBLE));
	}

	@Override
	protected void generateCharLiteral(char value) {
		stack.push(newLiteral(value, Type.CHAR));
	}

	@Override
	protected void generateBooleanLiteral(boolean value) {
		stack.push(newLiteral(value, Type.BOOLEAN));
	}

	@Override
	protected void generateVariableAccess(int variableIndex) {
		stack.push(newVariableAccess(variableIndex));
	}

	@Override
	protected void generateStaticFieldAccess(int classIndex, int fieldIndex) {
		stack.push(newStaticFieldAccess(classIndex, fieldIndex));
	}

	@Override
	protected void generateFieldAccess(int classIndex, int fieldIndex, boolean implicitThis) {
		final T classExpression = (implicitThis) ? null : stack.pop();
		stack.push(newFieldAccess(classIndex, fieldIndex, classExpression));
	}

	@Override
	protected void generateArrayAccess() {
		final T left = stack.pop();
		final T index = stack.pop();
		stack.push(newArrayAccess(left, index));
	}

	private List<T> getParameters() {
		List<T> params = pop(parameterCount);
		parameterCount = 0;
		return params;
	}

	private List<T> pop(int number) {
		List<T> result = new ArrayList<T>();
		if (number > 0) {
			List<T> sublist = stack.subList(stack.size() - number, stack.size());
			result.addAll(sublist);
			sublist.clear();
		}
		return result;
	}

}
