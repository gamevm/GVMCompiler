package com.gamevm.compiler.translator;

import java.util.Collection;
import java.util.LinkedList;

import sun.dc.pr.PRException;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.parser.ASTNode;
import com.gamevm.compiler.translator.ast.ASTTranslator;
import com.gamevm.compiler.translator.ast.Symbol;
import com.gamevm.compiler.translator.ast.SymbolTable;
import com.gamevm.execution.ast.tree.AbstractMethodInvocation;
import com.gamevm.execution.ast.tree.Assignment;
import com.gamevm.execution.ast.tree.Cast;
import com.gamevm.execution.ast.tree.Expression;
import com.gamevm.execution.ast.tree.ExpressionStatement;
import com.gamevm.execution.ast.tree.FieldAccess;
import com.gamevm.execution.ast.tree.IfStatement;
import com.gamevm.execution.ast.tree.Literal;
import com.gamevm.execution.ast.tree.MethodInvocation;
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
import com.gamevm.execution.ast.tree.TreeCodeInstruction;
import com.gamevm.execution.ast.tree.Variable;
import com.gamevm.execution.ast.tree.VariableDeclaration;
import com.gamevm.execution.ast.tree.WhileStatement;

public class TreeCodeTranslator extends ASTTranslator<TreeCodeInstruction> {

	public TreeCodeTranslator(SymbolTable symbolTable, boolean generateDebugInformation) {
		super(symbolTable, generateDebugInformation);
	}
	
	public TreeCodeInstruction pop() {
		return instructions.get(instructions.size() - 1);
	}
	
	public void push(TreeCodeInstruction i) {
		instructions.add(i);
	}
	
	private Expression getExpression(CodeSection s) {
		if (s == null)
			return null;
		return (Expression)instructions.get(s.getEndIndex());
	}
	
	private Collection<Expression> getExpressions(CodeSection[] sections) {
		Collection<Expression> pexpressions = new LinkedList<Expression>();
//		for (CodeSection s : sections) {
//			pexpressions.add(getExpression(s));
//		}
//		return pexpressions;
		for (int i = 0; i < sections.length; i++) {
			pexpressions.add((Expression)pop());
		}
		return pexpressions;
	}
	
	private Statement getStatement(CodeSection s) {
		TreeCodeInstruction i = instructions.get(s.getEndIndex());
		return getStatement(i);
	}
	
	private Statement getStatement(TreeCodeInstruction i) {
		if (i instanceof Statement)
			return (Statement)i;
		else if ((i instanceof Assignment) || (i instanceof AbstractMethodInvocation))
			return new ExpressionStatement((Expression)i);
		throw new IllegalArgumentException(i.getClass() + " is not a statement");
	}
	
	private ClassDeclaration getClassDeclaration(int classIndex) {
		return (classIndex >= 0) ? symbolTable.getClass(classIndex).getDeclaration() : symbolTable.getMainClass().getDeclaration();
	}

	@Override
	protected void generateLoop(CodeSection condition, CodeSection body) {
		push(new WhileStatement((Expression)pop(), getStatement(pop())));
	}

	@Override
	protected void generateBranch(CodeSection condition, CodeSection body, CodeSection alternative) {
		push(new IfStatement((Expression)pop(), getStatement(pop()), getStatement(pop())));
	}

	@Override
	protected void generateVariableInitialization(int variable, CodeSection initialization) {
		Symbol s = symbolTable.getSymbol(variable);
		Expression initExpression = (initialization != null) ? (Expression)pop() : null;
		push(new VariableDeclaration(variable, s.getType(), s.getName(), initExpression));
	}

	@Override
	protected void generateAssignment(CodeSection left, CodeSection right) {
		push(new Assignment((Expression)pop(), (Expression)pop()));
	}

	@Override
	protected void generateReturn(CodeSection expression) {
		push(new ReturnStatement((Expression)pop()));
	}

	@Override
	protected void generateStaticMethodInvocation(int classIndex, int methodIndex, CodeSection[] parameters) {
		ClassDeclaration parentClass = getClassDeclaration(classIndex);
		push(new StaticMethodInvocation(classIndex, methodIndex, getExpressions(parameters), parentClass));
	}

	@Override
	protected void generateMethodInvocation(int classIndex, int methodIndex, CodeSection classExpression, CodeSection[] parameters) {
		ClassDeclaration parentClass = getClassDeclaration(classIndex);
		push(new MethodInvocation(classIndex, (Expression)pop(), methodIndex, getExpressions(parameters), parentClass));
	}

	@Override
	protected void generateNewOperator(int classIndex, int methodIndex, CodeSection[] parameters) {
		ClassDeclaration type = getClassDeclaration(classIndex);
		push(new OpNew(classIndex, methodIndex, getExpressions(parameters), type));
	}

	@Override
	protected void generateNewArray(Type elementType, CodeSection[] dimensions) {
		push(new OpNewArray(elementType.getDefaultValue(), getExpressions(dimensions), elementType));
	}

	@Override
	protected void generateCast(Type origin, Type target, CodeSection expression) {
		push(new Cast((Expression)pop(), target));
	}

	@Override
	protected void generateBinaryOperation(int type, Type operationType, CodeSection left, CodeSection right) {
		Expression a = (Expression)pop();
		Expression b = (Expression)pop();
		switch (type) {
		case ASTNode.TYPE_OP_LAND:
			push(new OpLogicalAnd(a, b));
			break;
		case ASTNode.TYPE_OP_LOR:
			push(new OpLogicalOr(a, b));
			break;
		case ASTNode.TYPE_OP_EQU:
			push(new OpComparisonEquals(a, b));
			break;
		case ASTNode.TYPE_OP_NEQ:
			push(new OpComparisonUnequals(a, b));
			break;	
		case ASTNode.TYPE_OP_GTH:
		case ASTNode.TYPE_OP_LTH:
		case ASTNode.TYPE_OP_GEQ:
		case ASTNode.TYPE_OP_LEQ:
			if (operationType == Type.BYTE || operationType == Type.SHORT || operationType == Type.INT)
				push(new OpComparisonInteger(a, b, type));
			else if (operationType == Type.LONG)
				push(new OpComparisonLong(a, b, type));
			else if (operationType == Type.FLOAT)
				push(new OpComparisonFloat(a, b, type));
			else if (operationType == Type.DOUBLE)
				push(new OpComparisonDouble(a, b, type));
		case ASTNode.TYPE_OP_PLUS:
		case ASTNode.TYPE_OP_MINUS:
		case ASTNode.TYPE_OP_MULT:
		case ASTNode.TYPE_OP_DIV:
		case ASTNode.TYPE_OP_MOD:
			if (operationType == Type.BYTE || operationType == Type.SHORT || operationType == Type.INT)
				push(new OpArithInteger(a, b, type));
			else if (operationType == Type.LONG)
				push(new OpArithLong(a, b, type));
			else if (operationType == Type.FLOAT)
				push(new OpArithFloat(a, b, type));
			else if (operationType == Type.DOUBLE)
				push(new OpArithDouble(a, b, type));
		}
	}

	@Override
	protected void generateUnaryOperation(int type, Type operationType, CodeSection operand) {
		Expression a = (Expression)pop();
		switch (type) {
		case ASTNode.TYPE_OP_LNEG:
			push(new OpLNeg(a));
		case ASTNode.TYPE_OP_NEG:
			if (operationType == Type.BYTE || operationType == Type.SHORT || operationType == Type.INT)
				push(new OpNegInteger(a));
			else if (operationType == Type.LONG)
				push(new OpNegLong(a));
			else if (operationType == Type.FLOAT)
				push(new OpNegFloat(a));
			else if (operationType == Type.DOUBLE)
				push(new OpNegDouble(a));
		}
	}

	@Override
	protected void generateStringLiteral(String value) {
		push(new Literal(value));
	}

	@Override
	protected void generateIntegerLiteral(int value) {
		push(new Literal(value));
	}

	@Override
	protected void generateLongLiteral(long value) {
		push(new Literal(value));
	}

	@Override
	protected void generateFloatLiteral(float value) {
		push(new Literal(value));
	}

	@Override
	protected void generateDoubleLiteral(double value) {
		push(new Literal(value));
	}

	@Override
	protected void generateCharLiteral(char value) {
		push(new Literal(value));
	}

	@Override
	protected void generateBooleanLiteral(boolean value) {
		push(new Literal(value));
	}

	@Override
	protected void generateVariableAccess(int variableIndex) {
		push(new Variable(variableIndex, symbolTable.getSymbol(variableIndex).getName()));
	}

	@Override
	protected void generateStaticFieldAccess(int classIndex, int fieldIndex) {
		ClassDeclaration type = getClassDeclaration(classIndex);
		push(new StaticFieldAccess(classIndex, fieldIndex, type.getName(), type.getField(fieldIndex).getName()));
	}

	@Override
	protected void generateFieldAccess(int classIndex, int fieldIndex, CodeSection classExpression) {
		ClassDeclaration type = getClassDeclaration(classIndex);
		push(new FieldAccess(type, (Expression)pop(), fieldIndex));
	}

	@Override
	protected void generateArrayAccess(CodeSection left, CodeSection index) {
		push(new OpArrayAccess((Expression)pop(), (Expression)pop()));
	}

	@Override
	public Class<TreeCodeInstruction> getTargetInstructionType() {
		return TreeCodeInstruction.class;
	}

	

}
