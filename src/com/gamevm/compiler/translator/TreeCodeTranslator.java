package com.gamevm.compiler.translator;

import java.util.ArrayList;
import java.util.List;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.code.ExecutableTreeCode;
import com.gamevm.compiler.assembly.code.TreeCode;
import com.gamevm.compiler.parser.ASTNode;
import com.gamevm.compiler.translator.ast.ClassSymbol;
import com.gamevm.compiler.translator.ast.Symbol;
import com.gamevm.compiler.translator.ast.SymbolTable;
import com.gamevm.execution.ast.tree.Assignment;
import com.gamevm.execution.ast.tree.Block;
import com.gamevm.execution.ast.tree.Cast;
import com.gamevm.execution.ast.tree.CodeNode;
import com.gamevm.execution.ast.tree.CustomBinaryOperator;
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
import com.gamevm.execution.ast.tree.Variable;
import com.gamevm.execution.ast.tree.VariableDeclaration;
import com.gamevm.execution.ast.tree.WhileStatement;
import com.gamevm.utils.ListAdapter;

public class TreeCodeTranslator extends TreeTranslator<CodeNode> {

	public TreeCodeTranslator(SymbolTable symbolTable) {
		super(symbolTable);
	}

	private Statement getStatement(CodeNode n) {
		if (n instanceof Statement) {
			return (Statement) n;
		} else {
			return new ExpressionStatement((Expression) n);
		}
	}

	private List<Statement> getStatements(List<CodeNode> nodes) {
		List<Statement> statements = new ArrayList<Statement>(nodes.size());
		for (CodeNode n : nodes) {
			statements.add(getStatement(n));
		}
		return statements;
	}

	@Override
	protected CodeNode newBlock(List<CodeNode> body) {
		return new Block(getStatements(body));
	}

	@Override
	protected CodeNode newLoop(CodeNode condition, CodeNode body) {
		return new WhileStatement((Expression) condition, getStatement(body));
	}

	@Override
	protected CodeNode newBranch(CodeNode condition, CodeNode body, CodeNode alternative) {
		return new IfStatement((Expression) condition, getStatement(body), getStatement(alternative));
	}

	@Override
	protected CodeNode newVariableInitialization(int varIndex, CodeNode initialization) {
		Symbol s = symbolTable.getSymbol(varIndex);
		return new VariableDeclaration(varIndex, s.getType(), s.getName(), (Expression) initialization);
	}

	@Override
	protected CodeNode newReturn(CodeNode expression) {
		return new ReturnStatement((Expression) expression);
	}

	@Override
	protected CodeNode newAssignment(CodeNode lexpr, CodeNode rexpr) {
		return new Assignment((Expression) lexpr, (Expression) rexpr);
	}

	@Override
	protected CodeNode newStaticMethodInvocation(int classIndex, int methodIndex, List<CodeNode> parameters) {
		ClassDeclaration parentClass = symbolTable.getClass(classIndex).getDeclaration();
		return new StaticMethodInvocation(classIndex, methodIndex, new ListAdapter<Expression>(parameters), parentClass);
	}

	@Override
	protected CodeNode newMethodInvocation(int classIndex, int methodIndex, CodeNode classExpression,
			List<CodeNode> parameters) {
		ClassDeclaration parentClass = symbolTable.getClass(classIndex).getDeclaration();
		return new MethodInvocation(classIndex, (Expression) classExpression, methodIndex, new ListAdapter<Expression>(
				parameters), parentClass);
	}

	@Override
	protected CodeNode newNewOperator(int classIndex, int methodIndex, List<CodeNode> parameters) {
		ClassDeclaration parentClass = symbolTable.getClass(classIndex).getDeclaration();
		return new OpNew(classIndex, methodIndex, new ListAdapter<Expression>(parameters), parentClass);
	}

	@Override
	protected CodeNode newNewArray(Type elementType, List<CodeNode> sizeExpressions) {
		return new OpNewArray(elementType.getDefaultValue(), new ListAdapter<Expression>(sizeExpressions), elementType);
	}

	@Override
	protected CodeNode newUnaryOperator(int type, Type operationType, CodeNode operand) {
		Expression a = (Expression) operand;
		switch (type) {
		case ASTNode.TYPE_OP_LNEG:
			return new OpLNeg(a);
		case ASTNode.TYPE_OP_NEG:
			if (operationType == Type.BYTE || operationType == Type.SHORT || operationType == Type.INT)
				return new OpNegInteger(a);
			else if (operationType == Type.LONG)
				return new OpNegLong(a);
			else if (operationType == Type.FLOAT)
				return new OpNegFloat(a);
			else if (operationType == Type.DOUBLE)
				return new OpNegDouble(a);
		}
		return null;
	}

	@Override
	protected CodeNode newBinaryOperator(OperatorType type, int operator, Type operationType, CodeNode left,
			CodeNode right) {
		Expression a = (Expression) left;
		Expression b = (Expression) right;
		if (type == OperatorType.PRIMITIVE) {
			switch (operator) {
			case ASTNode.TYPE_OP_LAND:
				return new OpLogicalAnd(a, b);
			case ASTNode.TYPE_OP_LOR:
				return new OpLogicalOr(a, b);
			case ASTNode.TYPE_OP_EQU:
				return new OpComparisonEquals(a, b);
			case ASTNode.TYPE_OP_NEQ:
				return new OpComparisonUnequals(a, b);
			case ASTNode.TYPE_OP_GTH:
			case ASTNode.TYPE_OP_LTH:
			case ASTNode.TYPE_OP_GEQ:
			case ASTNode.TYPE_OP_LEQ:
				if (operationType == Type.BYTE || operationType == Type.SHORT || operationType == Type.INT)
					return new OpComparisonInteger(a, b, operator);
				else if (operationType == Type.LONG)
					return new OpComparisonLong(a, b, operator);
				else if (operationType == Type.FLOAT)
					return new OpComparisonFloat(a, b, operator);
				else if (operationType == Type.DOUBLE)
					return new OpComparisonDouble(a, b, operator);
			case ASTNode.TYPE_OP_PLUS:
			case ASTNode.TYPE_OP_MINUS:
			case ASTNode.TYPE_OP_MULT:
			case ASTNode.TYPE_OP_DIV:
			case ASTNode.TYPE_OP_MOD:
				if (operationType == Type.BYTE || operationType == Type.SHORT || operationType == Type.INT)
					return new OpArithInteger(a, b, operator);
				else if (operationType == Type.LONG)
					return new OpArithLong(a, b, operator);
				else if (operationType == Type.FLOAT)
					return new OpArithFloat(a, b, operator);
				else if (operationType == Type.DOUBLE)
					return new OpArithDouble(a, b, operator);
			}
		} else {
			ClassSymbol c = symbolTable.getClass(operationType);
			String opName = c.getDeclaration().getMethod(operator).getName();
			String opStr = null;
			if (opName.startsWith("operator"))
				opStr = opName.substring("operator".length());
			else
				opStr = opName.substring("loperator".length());
			return new CustomBinaryOperator(a, b, operator, type == OperatorType.CUSTOM_LEFT, opStr);
		}
		return null;
	}

	@Override
	protected CodeNode newLiteral(Object value, Type type) {
		return new Literal(value);
	}

	@Override
	protected CodeNode newVariableAccess(int index) {
		return new Variable(index, symbolTable.getSymbol(index).getName());
	}

	@Override
	protected CodeNode newStaticFieldAccess(int classIndex, int fieldIndex) {
		ClassSymbol c = symbolTable.getClass(classIndex);
		return new StaticFieldAccess(classIndex, fieldIndex, c.getName(), c.getDeclaration().getField(fieldIndex)
				.getName());
	}

	@Override
	protected CodeNode newFieldAccess(int classIndex, int fieldIndex, CodeNode classExpression) {
		ClassSymbol c = symbolTable.getClass(classIndex);
		return new FieldAccess(c.getDeclaration(), (Expression) classExpression, fieldIndex);
	}

	@Override
	protected CodeNode newArrayAccess(CodeNode left, CodeNode index) {
		return new OpArrayAccess((Expression) left, (Expression) index);
	}

	@Override
	protected CodeNode newCast(CodeNode expression, Type sourceType, Type targetType) {
		return new Cast((Expression) expression, targetType);
	}

	@Override
	protected TreeCode<CodeNode> getCode(CodeNode root) {
		return new ExecutableTreeCode(root);
	}

}
