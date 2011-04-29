package com.gamevm.compiler.translator;

import java.util.Collection;
import java.util.LinkedList;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.parser.ASTNode;
import com.gamevm.compiler.translator.ast.ASTTranslator;
import com.gamevm.compiler.translator.ast.Symbol;
import com.gamevm.compiler.translator.ast.SymbolTable;
import com.gamevm.execution.ast.tree.Assignment;
import com.gamevm.execution.ast.tree.Cast;
import com.gamevm.execution.ast.tree.Expression;
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
	
	private Expression getExpression(CodeSection s) {
		if (s == null)
			return null;
		return (Expression)instructions.get(s.getStartIndex());
	}
	
	private Collection<Expression> getExpressions(CodeSection[] sections) {
		Collection<Expression> pexpressions = new LinkedList<Expression>();
		for (CodeSection s : sections) {
			pexpressions.add(getExpression(s));
		}
		return pexpressions;
	}
	
	private Statement getStatement(CodeSection s) {
		return (Statement)instructions.get(s.getStartIndex());
	}
	
	private ClassDeclaration getClassDeclaration(int classIndex) {
		return (classIndex >= 0) ? symbolTable.getClass(classIndex).getDeclaration() : symbolTable.getMainClass().getDeclaration();
	}

	@Override
	protected void generateLoop(CodeSection condition, CodeSection body) {
		instructions.add(new WhileStatement(getExpression(condition), getStatement(body)));
	}

	@Override
	protected void generateBranch(CodeSection condition, CodeSection body, CodeSection alternative) {
		instructions.add(new IfStatement(getExpression(condition), getStatement(body), getStatement(alternative)));
	}

	@Override
	protected void generateVariableInitialization(int variable, CodeSection initialization) {
		Symbol s = symbolTable.getSymbol(variable);
		instructions.add(new VariableDeclaration(variable, s.getType(), s.getName(), getExpression(initialization)));
	}

	@Override
	protected void generateAssignment(CodeSection left, CodeSection right) {
		instructions.add(new Assignment(getExpression(left), getExpression(right)));
	}

	@Override
	protected void generateReturn(CodeSection expression) {
		instructions.add(new ReturnStatement(getExpression(expression)));
	}

	@Override
	protected void generateStaticMethodInvocation(int classIndex, int methodIndex, CodeSection[] parameters) {
		ClassDeclaration parentClass = getClassDeclaration(classIndex);
		instructions.add(new StaticMethodInvocation(classIndex, methodIndex, getExpressions(parameters), parentClass));
	}

	@Override
	protected void generateMethodInvocation(int classIndex, int methodIndex, CodeSection classExpression, CodeSection[] parameters) {
		ClassDeclaration parentClass = getClassDeclaration(classIndex);
		instructions.add(new MethodInvocation(classIndex, getExpression(classExpression), methodIndex, getExpressions(parameters), parentClass));
	}

	@Override
	protected void generateNewOperator(int classIndex, int methodIndex, CodeSection[] parameters) {
		ClassDeclaration type = getClassDeclaration(classIndex);
		instructions.add(new OpNew(classIndex, methodIndex, getExpressions(parameters), type));
	}

	@Override
	protected void generateNewArray(Type elementType, CodeSection[] dimensions) {
		instructions.add(new OpNewArray(elementType.getDefaultValue(), getExpressions(dimensions), elementType));
	}

	@Override
	protected void generateCast(Type origin, Type target, CodeSection expression) {
		instructions.add(new Cast(getExpression(expression), target));
	}

	@Override
	protected void generateBinaryOperation(int type, Type operationType, CodeSection left, CodeSection right) {
		Expression a = getExpression(left);
		Expression b = getExpression(right);
		switch (type) {
		case ASTNode.TYPE_OP_LAND:
			instructions.add(new OpLogicalAnd(a, b));
			break;
		case ASTNode.TYPE_OP_LOR:
			instructions.add(new OpLogicalOr(a, b));
			break;
		case ASTNode.TYPE_OP_EQU:
			instructions.add(new OpComparisonEquals(a, b));
			break;
		case ASTNode.TYPE_OP_NEQ:
			instructions.add(new OpComparisonUnequals(a, b));
			break;	
		case ASTNode.TYPE_OP_GTH:
		case ASTNode.TYPE_OP_LTH:
		case ASTNode.TYPE_OP_GEQ:
		case ASTNode.TYPE_OP_LEQ:
			if (operationType == Type.BYTE || operationType == Type.SHORT || operationType == Type.INT)
				instructions.add(new OpComparisonInteger(a, b, type));
			else if (operationType == Type.LONG)
				instructions.add(new OpComparisonLong(a, b, type));
			else if (operationType == Type.FLOAT)
				instructions.add(new OpComparisonFloat(a, b, type));
			else if (operationType == Type.DOUBLE)
				instructions.add(new OpComparisonDouble(a, b, type));
		case ASTNode.TYPE_OP_PLUS:
		case ASTNode.TYPE_OP_MINUS:
		case ASTNode.TYPE_OP_MULT:
		case ASTNode.TYPE_OP_DIV:
		case ASTNode.TYPE_OP_MOD:
			if (operationType == Type.BYTE || operationType == Type.SHORT || operationType == Type.INT)
				instructions.add(new OpArithInteger(a, b, type));
			else if (operationType == Type.LONG)
				instructions.add(new OpArithLong(a, b, type));
			else if (operationType == Type.FLOAT)
				instructions.add(new OpArithFloat(a, b, type));
			else if (operationType == Type.DOUBLE)
				instructions.add(new OpArithDouble(a, b, type));
		}
	}

	@Override
	protected void generateUnaryOperation(int type, Type operationType, CodeSection operand) {
		Expression a = getExpression(operand);
		switch (type) {
		case ASTNode.TYPE_OP_LNEG:
			instructions.add(new OpLNeg(a));
		case ASTNode.TYPE_OP_NEG:
			if (operationType == Type.BYTE || operationType == Type.SHORT || operationType == Type.INT)
				instructions.add(new OpNegInteger(a));
			else if (operationType == Type.LONG)
				instructions.add(new OpNegLong(a));
			else if (operationType == Type.FLOAT)
				instructions.add(new OpNegFloat(a));
			else if (operationType == Type.DOUBLE)
				instructions.add(new OpNegDouble(a));
		}
	}

	@Override
	protected void generateStringLiteral(String value) {
		instructions.add(new Literal(value));
	}

	@Override
	protected void generateIntegerLiteral(int value) {
		instructions.add(new Literal(value));
	}

	@Override
	protected void generateLongLiteral(long value) {
		instructions.add(new Literal(value));
	}

	@Override
	protected void generateFloatLiteral(float value) {
		instructions.add(new Literal(value));
	}

	@Override
	protected void generateDoubleLiteral(double value) {
		instructions.add(new Literal(value));
	}

	@Override
	protected void generateCharLiteral(char value) {
		instructions.add(new Literal(value));
	}

	@Override
	protected void generateBooleanLiteral(boolean value) {
		instructions.add(new Literal(value));
	}

	@Override
	protected void generateVariableAccess(int variableIndex) {
		instructions.add(new Variable(variableIndex, symbolTable.getSymbol(variableIndex).getName()));
	}

	@Override
	protected void generateStaticFieldAccess(int classIndex, int fieldIndex) {
		ClassDeclaration type = getClassDeclaration(classIndex);
		instructions.add(new StaticFieldAccess(classIndex, fieldIndex, type.getName(), type.getField(fieldIndex).getName()));
	}

	@Override
	protected void generateFieldAccess(int classIndex, int fieldIndex, CodeSection classExpression) {
		ClassDeclaration type = getClassDeclaration(classIndex);
		instructions.add(new FieldAccess(type, getExpression(classExpression), fieldIndex));
	}

	@Override
	protected void generateArrayAccess(CodeSection left, CodeSection index) {
		instructions.add(new OpArrayAccess(getExpression(left), getExpression(index)));
	}

	@Override
	public Class<TreeCodeInstruction> getTargetInstructionType() {
		return TreeCodeInstruction.class;
	}

	

}
