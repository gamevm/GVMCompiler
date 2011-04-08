tree grammar GCWalker;

options {
  language = Java;
  tokenVocab = GC;
  ASTLabelType = CommonTree;
}

@header {
package com.gamevm.compiler.parser;

import java.util.Map;
import java.util.HashMap;
}

@members {
}








program returns [List<Statement> result]:
	{ $result = new ArrayList<Statement>(); }
	(statement { $result.add($statement.result); })  *
;
	
statement returns [Statement result]:
	(
	  s=print_statement
	| s=assignment
	| s=if_statement
	| s=for_statement
	| s=while_statement
	| s=function_decl
	| s=return_statement
	| s=function_invocation
	| s=variable_init
	) 
	{ $result = $s.result; };
	
variable_init returns [Statement result]:
	^(VARIABLE_DECLARATION type IDENT expression?)
	{ $result = new VariableDeclarationStatement($type.result, $IDENT.text); }
	;
	
function_invocation returns [Statement result]:
	{ List<Expression> parameters = new ArrayList<Expression>(); }
	^(name=IDENT (expression { parameters.add($expression.result); } )*)
	{ $result = new FunctionInvocation($name.text, parameters); };
	
function_decl returns [Statement result]:
	^(FUNCTION_DECLARATION name=IDENT type parameter_list body) 
	{
		$result = new FunctionDeclaration($type.result, $name.text, $parameter_list.result, $body.result);
		
	};
	
parameter_list returns [List<String> result]:
{ $result = new ArrayList<String>(); }
	^(PARAMETER_LIST (type name=IDENT { $result.add($name.text); })*)
	;
	
for_statement returns [Statement result]:
	{ List<Statement> increment = new ArrayList<Statement>(); }
	^('for' assignment expression (statement { increment.add($statement.result); })* body)
  { $result = new ForStatement($body.result, $assignment.result, $expression.result, increment); }
;

while_statement returns [Statement result]:
	^('while' expression body) { $result = new WhileStatement($expression.result, $body.result); };

if_statement returns [Statement result]:
	{ List<IfClause> clauses = new ArrayList<IfClause>(); }
	^('if' expression body { clauses.add(new IfClause($expression.result, $body.result)); }
	 (else_if_statement { clauses.add($else_if_statement.result); })* (else_statement { clauses.add($else_statement.result); })?)
	{ $result = new IfStatement(clauses); };
	
	
else_if_statement returns [IfClause result]:
	^(ELSE_IF_CLAUSE expression body) { $result = new IfClause($expression.result, $body.result); }
;

else_statement returns [IfClause result]:
	^('else' body) { $result = new IfClause(null, $body.result); }
;

return_statement returns [Statement result]:
	^('return' expression) { $result = new ReturnStatement($expression.result); }
	;
	
body returns [List<Statement> result]:
	{ $result = new ArrayList<Statement>(); }
	^(BODY 
	(statement { $result.add($statement.result); })
	*)
	;
	
type returns [Class<?> result]:
	  'byte' { $result = Byte.class; }
	| 'short' { $result = Short.class; }
	| 'char' { $result = Character.class; }
	| 'int' { $result = Integer.class; }
	| 'long' { $result = Long.class; }
	| 'float' { $result = Float.class; }
	| 'double' { $result = Double.class; }
	| 'boolean' { $result = Boolean.class; }
	| IDENT { $result = Object.class; }
	
	;

print_statement returns [Statement result]:
	^('print' expression) { $result = new PrintStatement($expression.result); }
	;

expression returns [Expression result]:
	  ^('&&' op1=expression op2=expression) { $result = new BinaryExpression($op1.result, $op2.result, BinaryExpression.OPERATOR_LAN); }
	| ^('||' op1=expression op2=expression) { $result = new BinaryExpression($op1.result, $op2.result, BinaryExpression.OPERATOR_LOR); }
	| ^('!=' op1=expression op2=expression) { $result = new BinaryExpression($op1.result, $op2.result, BinaryExpression.OPERATOR_NEQ); }
	| ^('==' op1=expression op2=expression) { $result = new BinaryExpression($op1.result, $op2.result, BinaryExpression.OPERATOR_EQU); }
	| ^('>=' op1=expression op2=expression) { $result = new BinaryExpression($op1.result, $op2.result, BinaryExpression.OPERATOR_GEQ); }
	| ^('<=' op1=expression op2=expression) { $result = new BinaryExpression($op1.result, $op2.result, BinaryExpression.OPERATOR_LEQ); }
	| ^('>' op1=expression op2=expression) { $result = new BinaryExpression($op1.result, $op2.result, BinaryExpression.OPERATOR_GTH); }
	| ^('<' op1=expression op2=expression) { $result = new BinaryExpression($op1.result, $op2.result, BinaryExpression.OPERATOR_LTH); }
	| ^('+' op1=expression op2=expression) { $result = new BinaryExpression($op1.result, $op2.result, BinaryExpression.OPERATOR_ADD); }
	| ^('-' op1=expression op2=expression) { $result = new BinaryExpression($op1.result, $op2.result, BinaryExpression.OPERATOR_SUB); }
	| ^('/' op1=expression op2=expression) { $result = new BinaryExpression($op1.result, $op2.result, BinaryExpression.OPERATOR_DIV); }
	| ^('*' op1=expression op2=expression) { $result = new BinaryExpression($op1.result, $op2.result, BinaryExpression.OPERATOR_MUL); }
	| ^('%' op1=expression op2=expression) { $result = new BinaryExpression($op1.result, $op2.result, BinaryExpression.OPERATOR_MOD); }
	| ^(NEGATION op1=expression) { $result = new NumericNegation($op1.result); }
	| ^('!' op1=expression) { $result = new LogicalNegation($op1.result); }
	| IDENT { $result = new VariableExpression($IDENT.text); }
	| INTEGER_LITERAL { $result = new Literal<Long>(Long.parseLong($INTEGER_LITERAL.text)); }
	| STRING_LITERAL { $result = new Literal<String>($STRING_LITERAL.text); }
	| CHAR_LITERAL { $result = new Literal<Character>($CHAR_LITERAL.text.charAt(0)); }
	| function_invocation { $result = (Expression)$function_invocation.result; }
	| 'readInt' { $result = new ReadInt(); }
	| 'readLine' { $result = new ReadString(); }
	| BUILTIN_VAR { $result = new VariableExpression($BUILTIN_VAR.text); }
	;
	
	
assignment returns [Statement result]:
	^('=' IDENT expression) { $result = new Assignment($IDENT.text, $expression.result); }
	;
	
	
